package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

import java.util.List;

import javax.persistence.EntityManager;


public interface TransactionEntityFacade {

    String createTransaction(String type, Account account, int amount, String status, EntityManager em);

    List<Transaction> getTransactions(long accountId);
}
