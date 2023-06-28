package com.tm.loanapi.model;

import jakarta.persistence.*;

@Entity
@NamedQuery(name = "Loan.findLast", query = "select l from Loan l where l.telephone = ?1 order by id desc limit 1")
public class Loan {
    private int id;
    private String telephone;
    private int amount;
    private long timestamp;
    private int balance;

    public Loan() {
    }

    public Loan(String telephone, int amount, long timestamp) {
        this.telephone = telephone;
        this.amount = amount;
        this.timestamp = timestamp;
        balance = amount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
