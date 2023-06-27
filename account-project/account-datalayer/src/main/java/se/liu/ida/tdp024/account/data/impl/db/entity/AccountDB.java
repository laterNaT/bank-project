package se.liu.ida.tdp024.account.data.impl.db.entity;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AccountDB implements Account {

    @Id
    @GeneratedValue()
    private long id;
    private String personKey;
    private String bankKey;
    private String accountType;
    private int holdings;

    @Override
    public String getAccountType() {
        return this.accountType;
    }

    @Override
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public int getHoldings() {
        return this.holdings;
    }

    @Override
    public void setHoldings(int amount) {
        this.holdings = amount;
    }

    @Override
    public String getBankKey() {
        return this.bankKey;
    }

    @Override
    public void setBankKey(String bankKey) {
        this.bankKey = bankKey;
    }

    @Override
    public String getPersonKey() {
        return this.personKey;
    }

    @Override
    public void setPersonKey(String personKey) {
        this.personKey = personKey;
    }

    @Override
    public long getId() {
        return this.id;
    }

}
