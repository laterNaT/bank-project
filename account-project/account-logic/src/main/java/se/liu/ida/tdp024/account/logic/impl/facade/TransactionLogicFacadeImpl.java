package se.liu.ida.tdp024.account.logic.impl.facade;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;

import java.util.List;

public class TransactionLogicFacadeImpl implements TransactionLogicFacade {

    private final TransactionEntityFacade transactionEntityFacade;

    public TransactionLogicFacadeImpl(TransactionEntityFacade transactionEntityFacade) {
        this.transactionEntityFacade = transactionEntityFacade;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(long accountId) {
        return this.transactionEntityFacade.getTransactions(accountId);
    }
}
