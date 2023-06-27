package se.liu.ida.tdp024.account.data.test.facade;

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

import static org.junit.Assert.assertEquals;

public class AccountEntityFacadeTest {

    private TransactionEntityFacade transactionEntityFacade;
    private AccountEntityFacade accountEntityFacade;
    private StorageFacade storageFacade;

    @Before
    public void setup() {
        transactionEntityFacade = new TransactionEntityFacadeDB();
        accountEntityFacade = new AccountEntityFacadeDB(transactionEntityFacade);
        storageFacade = new StorageFacadeDB();
    }

    @After
    public void tearDown() {
       storageFacade.emptyStorage();
    }

    @Test
    public void createAccount_ForSavingsAccount_ReturnsOK() {
        String result = accountEntityFacade.createAccount("SAVINGS", "2", "4");
        assertEquals("OK", result);
    }
    @Test
    public void getAccounts_WhenNoAccountsCreated_EmptyList() {
        List<Account> result = accountEntityFacade.getAccounts("2");
        assertEquals(0, result.size());
    }

    @Test
    public void getAccounts_WhenOneAccountExists_ListWithOneAccount() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        List<Account> result = accountEntityFacade.getAccounts("4");
        assertEquals(1, result.size());
        assertEquals("SAVINGS", result.get(0).getAccountType());
        assertEquals("2", result.get(0).getBankKey());
        assertEquals("4", result.get(0).getPersonKey());
    }

    @Test
    public void getAccounts_WhenTwoAccountsExists_ListWithTwoAccount() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        accountEntityFacade.createAccount("CHECK", "3", "4");
        List<Account> result = accountEntityFacade.getAccounts("4");
        assertEquals(2, result.size());
        assertEquals("SAVINGS", result.get(0).getAccountType());
        assertEquals("CHECK", result.get(1).getAccountType());
        assertEquals("2", result.get(0).getBankKey());
        assertEquals("3", result.get(1).getBankKey());
        assertEquals("4", result.get(0).getPersonKey());
        assertEquals("4", result.get(1).getPersonKey());
    }

    @Test
    public void credit_PositiveAmount_OK() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        long id = accountEntityFacade.getAccounts("4").get(0).getId();
        String result = accountEntityFacade.credit(id, 50);
        assertEquals("OK", result);
        assertEquals(50, accountEntityFacade.getAccounts("4").get(0).getHoldings());
    }

    @Test
    public void credit_NegativeAmount_FAILED() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        long id = accountEntityFacade.getAccounts("4").get(0).getId();
        String result = accountEntityFacade.credit(id, -50);
        assertEquals("FAILED", result);
        assertEquals(0, accountEntityFacade.getAccounts("4").get(0).getHoldings());
    }

    @Test
    public void debit_PositiveAmountWithEnoughFunds_OK() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        long id = accountEntityFacade.getAccounts("4").get(0).getId();
        accountEntityFacade.credit(id, 50);
        String result = accountEntityFacade.debit(id, 40);
        assertEquals("OK", result);
        assertEquals(10, accountEntityFacade.getAccounts("4").get(0).getHoldings());
    }

    @Test
    public void debit_PositiveAmountWithoutEnoughFunds_FAILED() {
        accountEntityFacade.createAccount("SAVINGS", "2", "4");
        long id = accountEntityFacade.getAccounts("4").get(0).getId();
        accountEntityFacade.credit(id, 50);
        String result = accountEntityFacade.debit(id, 60);
        assertEquals("FAILED", result);
        assertEquals(50, accountEntityFacade.getAccounts("4").get(0).getHoldings());
    }
}
