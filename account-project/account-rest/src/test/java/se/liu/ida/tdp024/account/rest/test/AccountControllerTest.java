package se.liu.ida.tdp024.account.rest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.rest.AccountController;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccountControllerTest {

    private AccountController accountController;
    private StorageFacade storageFacade;


    @Before
    public void initialize() {
        accountController = new AccountController();
        storageFacade = new StorageFacadeDB();
    }

    @After
    public void tearDown() {
        if (storageFacade != null)
            storageFacade.emptyStorage();
    }

    @Test
    public void testCreate() {
        ResponseEntity<String> result = accountController.create("1", "SAVINGS", "JPMORGAN");
        assertEquals("OK", result.getBody());

        result = accountController.create("5", "CHECK", "NORDEA");
        assertEquals("OK", result.getBody());

        result = accountController.create("", "", "");
        assertEquals("FAILED", result.getBody());

        result = accountController.create("10", "CHECK", "JPMORGAN");
        assertEquals("FAILED", result.getBody());

        result = accountController.create("5", "CHECK", "JP");
        assertEquals("FAILED", result.getBody());

    }

    @Test
    public void testFindPerson() {
        accountController.create("1", "SAVINGS", "JPMORGAN");
        ResponseEntity<List<Account>> result = accountController.findPerson("1");
        assertEquals(1, result.getBody().size());
        assertEquals("1", result.getBody().get(0).getPersonKey());
    }

    @Test
    public void testCredit() {
        accountController.create("1", "SAVINGS", "JPMORGAN");
        long id = accountController.findPerson("1").getBody().get(0).getId();
        ResponseEntity<String> result = accountController.credit(id, 10);
        assertEquals("OK", result.getBody());
        Account account = accountController.findPerson("1").getBody().get(0);
        assertEquals(10, account.getHoldings());
    }

    @Test
    public void testDebit() {
        accountController.create("1", "SAVINGS", "JPMORGAN");
        long id = accountController.findPerson("1").getBody().get(0).getId();
        accountController.credit(id, 50);
        ResponseEntity<String> result = accountController.debit(id, 25);
        Account account = accountController.findPerson("1").getBody().get(0);
        assertEquals("OK", result.getBody());
        assertEquals(25, account.getHoldings());

    }

    @Test
    public void testTransactions() {
        testCredit();
        ResponseEntity<List<Transaction>> result = accountController.transactions(1);
        assertEquals(1, result.getBody().size());
        assertEquals(2, result.getBody().get(0).getID());
    }

    @Test
    public void testHandleError() {
        String result = accountController.handleError();
        assertEquals("", result);
    }
}

