package se.liu.ida.tdp024.account.data.impl.db.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelper;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelperImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class AccountEntityFacadeDB implements AccountEntityFacade {

    private KafkaHelper kafkaHelperImpl = new KafkaHelperImpl();
    private final TransactionEntityFacade transactionEntityFacade;

    public AccountEntityFacadeDB(TransactionEntityFacade transactionEntityFacade) {
        this.transactionEntityFacade = transactionEntityFacade;
    }

    @Override
    public String createAccount(String accountType, String bankKey, String personKey) {
        EntityManager em = EMF.getEntityManager();

        try {
            em.getTransaction().begin();
            Account account = new AccountDB();
            account.setHoldings(0); // no holdings initially
            account.setAccountType(accountType);
            account.setBankKey(bankKey);
            account.setPersonKey(personKey);
            em.persist(account);
            em.getTransaction().commit();
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[OK] account with id " + account.getId() + " created in database");
            return "OK";
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println(e);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC, 
                "[FAILED] to create account using bankKey " + bankKey + " and personKey " + personKey + " in database");
            return "FAILED";
        } finally {
            em.close();
        }
    }

    @Override
    public List<Account> getAccounts(String personKey) {
        EntityManager em = EMF.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM AccountDB a WHERE a.personKey = :personKey", Account.class)
                    .setParameter("personKey", personKey);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[OK] returning " + query.getResultList().size() + " accounts from database with personKey " + personKey);
            return query.getResultList();
        } catch (Exception e) {
            System.out.println(e);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[FAILED] getting accounts from database with personKey " + personKey);
            return new ArrayList<Account>();
        } finally {
            em.close();
        }
    }

    @Override
    public String debit(long accountId, int amount) {
        EntityManager em = EMF.getEntityManager();
        Account account = em.find(AccountDB.class, accountId);
        String status = "FAILED";
        try {
            em.getTransaction().begin();
            em.lock(account, LockModeType.PESSIMISTIC_WRITE);
            int currentHoldings = account.getHoldings();
            int newHoldings = currentHoldings - amount;

            if (newHoldings < 0) {
                throw new IllegalArgumentException("Holdings less than zero is not allowed");
            }

            account.setHoldings(newHoldings);
            em.persist(account);
            
            status = "OK";
            transactionEntityFacade.createTransaction("DEBIT", account, amount, status, em);
            em.getTransaction().commit();
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC, 
                "[OK] debit transaction of " + amount + " created in database for accountid" + account.getId());

        } catch (IllegalArgumentException e) {
            System.out.println(e);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[FAILED] debit transaction of " + amount + "created in database for accountid " + account.getId());

                        transactionEntityFacade.createTransaction("DEBIT", account, amount, status, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println((e));
        } finally {
            em.close();
        }
        return status;
    }

    @Override
    public String credit(long accountId, int amount) {
        EntityManager em = EMF.getEntityManager();
        Account account = em.find(AccountDB.class, accountId);
        String status = "FAILED";
        try {
            em.getTransaction().begin();
            em.lock(account, LockModeType.PESSIMISTIC_WRITE);
            if (amount < 0) {
                throw new IllegalArgumentException("Crediting a negative amount is not allowed");
            }

            int currentHoldings = account.getHoldings();
            int newHoldings = currentHoldings + amount;

            account.setHoldings(newHoldings);
            em.persist(account);

            status = "OK";
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[OK] credit transaction of " + amount + " created in database for accountid" + account.getId());
            transactionEntityFacade.createTransaction("CREDIT", account, amount, status, em);
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            transactionEntityFacade.createTransaction("CREDIT", account, amount, status, em);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,       
                "[FAILED] credit transaction of " + amount + "created in database for accountid " + account.getId());
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println((e));
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
        return status;
    }
}
