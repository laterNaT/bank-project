package se.liu.ida.tdp024.account.data.impl.db.facade;


import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelper;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelperImpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {
    
    private KafkaHelper kafkaHelperImpl = new KafkaHelperImpl();

    @Override
    public String createTransaction(String type, Account account, int amount, String status, EntityManager em) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        try {
            Transaction transaction = new TransactionDB();
            transaction.setAmount(amount);
            transaction.setCreated(ft.format(date));
            transaction.setAccount(account);
            transaction.setType(type);
            transaction.setStatus(status);
            em.persist(transaction);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[" + status.toUpperCase() + "] " + type + " transaction of " + amount + "$ created for account " + account.getId() + " in transactionDB");
            return "OK";
        } catch(Exception e) {
            em.getTransaction().rollback();
            System.out.println(e);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC, 
            "[" + status.toUpperCase() + "]" + " couldn't create " + type + " Transaction of " + amount + "$ for account " + account.getId() + " in transactionDB, ROLLING BACK TRANSACTION");
            return "FAILED";
        }
    }

    @Override
    public List<Transaction> getTransactions(long accountId) {
        EntityManager em = EMF.getEntityManager();
        Account account = em.find(AccountDB.class, accountId);
        try {
            TypedQuery<Transaction> query =
                    em
                    .createQuery("SELECT t FROM TransactionDB t WHERE t.account = :account", Transaction.class)
                    .setParameter("account", account);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[OK] returning " + query.getResultList().size() + "transactions from database for accountid " + account.getId());
            return query.getResultList();
        } catch (Exception e) {
            System.out.println(e);
            kafkaHelperImpl.sendMessage(KafkaHelperImpl.TRANSACTION_TOPIC,
                "[FAILED] getting transactions from database for accountid " + accountId);
            return new ArrayList<Transaction>();
        } finally {
            em.close();
        }

    }
}
