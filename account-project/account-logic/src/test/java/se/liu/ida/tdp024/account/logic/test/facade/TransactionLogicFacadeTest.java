package se.liu.ida.tdp024.account.logic.test.facade;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadeImpl;

public class TransactionLogicFacadeTest {
    
    //--- Unit under test ---//
    private TransactionEntityFacade transactionEntityFacade;
    private AccountEntityFacade accountEntityFacade;
    private AccountLogicFacade accountLogicFacade;
    private TransactionLogicFacade transactionLogicFacade;
    private StorageFacade storageFacade;

    
    @Before
    public void initialize() {
      transactionEntityFacade = new TransactionEntityFacadeDB();
      accountEntityFacade = new AccountEntityFacadeDB(transactionEntityFacade);
      accountLogicFacade = new AccountLogicFacadeImpl(accountEntityFacade);
      transactionLogicFacade = new TransactionLogicFacadeImpl(transactionEntityFacade);
      storageFacade = new StorageFacadeDB();

    }

    @After
    public void tearDown() {
      if (storageFacade != null)
        storageFacade.emptyStorage();
    }

    @Test
    public void testGetTransactionsByAccountId() {
        accountLogicFacade.createAccount("CHECK", "NORDEA", "1");
        Long id = accountLogicFacade.getAccountsByPersonKey("1").get(0).getId();
        accountLogicFacade.credit(id, 10);
        List<Transaction> result = transactionLogicFacade.getTransactionsByAccountId(id);
        assert(result.size() == 1);
        assert(result.get(0).getAmount() == 10);
    }
}
