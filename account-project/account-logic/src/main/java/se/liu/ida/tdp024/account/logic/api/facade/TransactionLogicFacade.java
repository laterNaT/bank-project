package se.liu.ida.tdp024.account.logic.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;

import java.util.List;

public interface TransactionLogicFacade {

    List<Transaction> getTransactionsByAccountId(long accountId);
}
