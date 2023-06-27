package se.liu.ida.tdp024.account.logic.api.facade;


import se.liu.ida.tdp024.account.data.api.entity.Account;

import java.util.List;

public interface AccountLogicFacade {
    List<Account> getAccountsByPersonKey(String personKey);

    String createAccount(String accountType, String bankName, String personKey);

    String debit(long accountId, int amount);

    String credit(long accountId, int amount);
}
