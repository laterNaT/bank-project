
package se.liu.ida.tdp024.account.data.test.facade;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;

import static org.junit.Assert.assertEquals;

public class TransactionEntityFacadeTest {

    private TransactionEntityFacade transactionEntityFacade;
    private AccountEntityFacade accountEntityFacade;
    private StorageFacade storageFacade;
    private Account account;

    @Before
    public void initialize() {
        transactionEntityFacade = new TransactionEntityFacadeDB();
        accountEntityFacade = new AccountEntityFacadeDB(transactionEntityFacade);
        storageFacade = new StorageFacadeDB();

        accountEntityFacade.createAccount("SAVINGS", "1", "1");
        account = accountEntityFacade.getAccounts("1").get(0);
    }

    @After
    public void tearDown() {
       storageFacade.emptyStorage();
    }

    // @Test
    // public void createTransaction_ForCreditAccount_OK() {

    //     String status = transactionEntityFacade.createTransaction("CREDIT", account, 10, "OK", null);
    //     assertEquals("OK", status);
    // }

    @Test
    public void getTransactions_ForTransactionAlreadyCreated_CorrectList() {
        // accountEntityFacade.createAccount("SAVINGS", "1", "1");
        accountEntityFacade.credit(account.getId(), 20);
        List<Transaction> result = transactionEntityFacade.getTransactions(account.getId());
        assertEquals(1, result.size());
        assertEquals("OK", result.get(0).getStatus());
        assertEquals(2, result.get(0).getID());
        assertEquals(20, result.get(0).getAmount());
        assertEquals(transactionEntityFacade.getTransactions(account.getId()).get(0).getCreated(), result.get(0).getCreated());
    }
}
