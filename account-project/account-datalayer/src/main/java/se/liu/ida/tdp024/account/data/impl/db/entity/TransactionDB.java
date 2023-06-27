package se.liu.ida.tdp024.account.data.impl.db.entity;


import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TransactionDB implements Transaction {

    @Id
    @GeneratedValue
    private long id;
    private String type;
    private int amount;
    private String created;
    private String status;

    @ManyToOne(targetEntity = AccountDB.class)
    private Account account;


    @Override
    public long getID() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String getCreated() {
        return this.created;
    }

    @Override
    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }
}
