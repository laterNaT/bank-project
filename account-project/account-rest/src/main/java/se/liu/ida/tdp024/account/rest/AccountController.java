package se.liu.ida.tdp024.account.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.logic.api.facade.TransactionLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.logic.impl.facade.TransactionLogicFacadeImpl;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelper;
import se.liu.ida.tdp024.account.util.kafka.KafkaHelperImpl;

import java.util.List;

@RestController
@RequestMapping("/account-rest/account")
public class AccountController implements ErrorController {
    private final TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacadeDB();
    private final TransactionLogicFacade transactionLogicFacade = new TransactionLogicFacadeImpl(transactionEntityFacade);

    private final AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB(transactionEntityFacade);
    private final AccountLogicFacade accountLogicFacade = new AccountLogicFacadeImpl(accountEntityFacade);

    private KafkaHelper kafkaHelperImpl = new KafkaHelperImpl();

    private final String createRequestMsg = "Creating account";
    private final String createSuccessMsg = "Success, account created";
    private final String findRequestMsg = "Finding accounts";
    private final String findSuccessMsg = "Success, Accounts found";
    private final String debitRequestMsg = "Debiting account";
    private final String debitSuccessMsg = "Success, account debited";
    private final String creditRequestMsg = "Crediting account";
    private final String creditSuccessMsg = "Success, account credited";
    private final String transactionRequestMsg = "Finding transactions";
    private final String transactionSuccessMsg = "Success, transactions found";

    @RequestMapping("/error")
    public String handleError() {
        return "";
    }

    @RequestMapping("/find/person")
    public ResponseEntity<List<Account>> findPerson(@RequestParam(value="person") String personKey) {
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC, findRequestMsg + " personKey: " + personKey);
        List<Account> res = accountLogicFacade.getAccountsByPersonKey(personKey);
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC, findSuccessMsg + " personKey: " + personKey);
        return new ResponseEntity<List<Account>>(res, HttpStatus.OK);
    }

    @RequestMapping("/create")
    public ResponseEntity<String> create(
            @RequestParam(value="person", defaultValue = "") String personKey,
            @RequestParam(value="accounttype", defaultValue = "") String accountType,
            @RequestParam(value= "bank", defaultValue = "") String bankName) {

        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            createRequestMsg + " personKey: " + personKey + " accountType: " + accountType + " bankName: " + bankName);
        String res = accountLogicFacade.createAccount(accountType, bankName, personKey);
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            createSuccessMsg  + " personKey: " + personKey + " accountType: " + accountType + " bankName: " + bankName);
        return new ResponseEntity<String>(res, HttpStatus.OK);
    }
    
    @RequestMapping("/debit")
    public ResponseEntity<String> debit(
            @RequestParam(value="id") long accountId,
            @RequestParam(value="amount") int amount) {
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            debitRequestMsg + " accountId: " + accountId + " amount: " + amount);
        String res = accountLogicFacade.debit(accountId, amount);
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            debitSuccessMsg + " accountId: " + accountId + " amount: " + amount);
        return new ResponseEntity<String>(res,HttpStatus.OK);
    }

    @RequestMapping("/credit")
    public ResponseEntity<String> credit(
            @RequestParam(value="id") long accountId,
            @RequestParam(value="amount") int amount) {
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            creditRequestMsg + " accountId: " + accountId + " amount: " + amount);
        String res = accountLogicFacade.credit(accountId, amount);
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            creditSuccessMsg + " accountId: " + accountId + " amount: " + amount);
        return new ResponseEntity<String>(res,HttpStatus.OK);
    }


    @RequestMapping("/transactions")
    public ResponseEntity<List<Transaction>> transactions(@RequestParam(value="id") long accountId) {
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            transactionRequestMsg + " for accountId " + accountId);
        List<Transaction> res = transactionLogicFacade.getTransactionsByAccountId(accountId);
        kafkaHelperImpl.sendMessage(KafkaHelperImpl.REST_API_TOPIC,
            transactionSuccessMsg + " for accountId " + accountId);
        return new ResponseEntity<List<Transaction>>(res, HttpStatus.OK);

    }
}
