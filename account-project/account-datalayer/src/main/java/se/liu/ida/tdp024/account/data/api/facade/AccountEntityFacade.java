package se.liu.ida.tdp024.account.data.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import java.util.List;

public interface AccountEntityFacade {

    String createAccount(String accountType, String bankKey, String personKey);

    List<Account> getAccounts(String personKey);

    String debit(long accountId, int amount);

    String credit(long accountId, int amount);
}
