package se.liu.ida.tdp024.account.logic.impl.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.util.http.HTTPHelper;
import se.liu.ida.tdp024.account.util.http.HTTPHelperImpl;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializer;
import se.liu.ida.tdp024.account.util.json.AccountJsonSerializerImpl;

import java.util.HashMap;
import java.util.List;

public class AccountLogicFacadeImpl implements AccountLogicFacade {
    private static final HTTPHelper httpHelper = new HTTPHelperImpl();

    private final AccountJsonSerializer accountJsonSerializer = new AccountJsonSerializerImpl();
    private final AccountEntityFacade accountEntityFacade;

    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade) {
        this.accountEntityFacade = accountEntityFacade;
    }

    @Override
    public List<Account> getAccountsByPersonKey(String personKey) {
        return accountEntityFacade.getAccounts(personKey);
    }

    @Override
    public String createAccount(String accountType, String bankName, String personKey) {
        if (accountType.equals("") || bankName.equals("") || personKey.equals("")) {
            return "FAILED";
        }

        try {
            String bankApi = "http://localhost:8100/bank/";
            String bank = httpHelper.get(bankApi + "find/" + bankName);
            String personApi = "http://localhost:8090/person/";
            String person = httpHelper.get(personApi + "find.key/" + personKey);


            if (!(accountType.equals("SAVINGS") || accountType.equals("CHECK"))) {
                return "FAILED";
            }
            else if (person.isEmpty() || bank.isEmpty()) {
                return "FAILED";
            }

            String apiBankKey = accountJsonSerializer.fromJson(bank, HashMap.class).get("key").toString();
            String apiPersonKey = accountJsonSerializer.fromJson(person, HashMap.class).get("key").toString();

            return accountEntityFacade.createAccount(accountType, apiBankKey, apiPersonKey);
        } catch (Exception e) {
            System.out.println(e);
            return "FAILED";
        }
    }

    @Override
    public String debit(long accountId, int amount) {
        return accountEntityFacade.debit(accountId, amount);

    }

    @Override
    public String credit(long accountId, int amount) {
        return accountEntityFacade.credit(accountId, amount);
    }
}
