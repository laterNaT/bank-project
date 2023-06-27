package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;

public interface Transaction extends Serializable {

    long getID();

    String getType();
    void setType(String type);

    int getAmount();
    void setAmount(int amount);

    String getCreated();
    void setCreated(String date);

    String getStatus();
    void setStatus(String status);

    Account getAccount();
    void setAccount(Account account);

}
