package se.liu.ida.tdp024.account.logic.test.facade;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;

public class AccountLogicFacadeTest {


    //--- Unit under test ---//
    private TransactionEntityFacade transactionEntityFacade;
    private AccountEntityFacade accountEntityFacade;
    private AccountLogicFacade accountLogicFacade;
    private StorageFacade storageFacade;

    @Before
    public void initialize() {
      transactionEntityFacade = new TransactionEntityFacadeDB();
      accountEntityFacade = new AccountEntityFacadeDB(transactionEntityFacade);
      accountLogicFacade = new AccountLogicFacadeImpl(accountEntityFacade);
      storageFacade = new StorageFacadeDB();

    }

    @After
    public void tearDown() {
      if (storageFacade != null)
        storageFacade.emptyStorage();
    }



    @Test
    public void testCreateAccount() {
      String result = accountLogicFacade.createAccount("SAVINGS", "SWEDBANK", "3");
      assert(result.equals("OK"));
      
      result = accountLogicFacade.createAccount("SAVINGS", "SWEDBANK", "");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("CHECK", "", "3");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("", "", "3");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("SAVINGS", "HEJ", "3");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("CHECK", "NORDEA", "999");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("A", "JPMORGAN", "4");
      assert(result.equals("FAILED"));

      result = accountLogicFacade.createAccount("", "", "");
      assert(result.equals("FAILED"));

    }

    @Test
    public void testGetAccountsByPersonKey() {
      accountLogicFacade.createAccount("SAVINGS", "SWEDBANK", "3");
      List<Account> result = accountLogicFacade.getAccountsByPersonKey("3");
      assert(result.size() == 1);
      assert(result.get(0).getPersonKey().equals("3"));
    }

    @Test
    public void testCreditDebit() {
      accountLogicFacade.createAccount("SAVINGS", "SWEDBANK", "3");
      Account account = accountLogicFacade.getAccountsByPersonKey("3").get(0);
      String result = accountLogicFacade.credit(account.getId(), 20);
      assert(result.equals("OK"));
      
      result = accountLogicFacade.debit(account.getId(), 20);
      assert(result.equals("OK"));
    }
}
