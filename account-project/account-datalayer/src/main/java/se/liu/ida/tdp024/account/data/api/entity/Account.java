package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Account extends Serializable {
    String getAccountType();
    void setAccountType(String accountType);

    int getHoldings();
    void setHoldings(int amount);

    String getBankKey();
    void setBankKey(String bankKey);

    String getPersonKey();
    void setPersonKey(String personKey);

    long getId();
}
