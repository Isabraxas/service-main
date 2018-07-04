package com.viridian.service.statement.persistence.auto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.viridian.service.statement.persistence.Transaction;

/**
 * Class _Account was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Account extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<BigDecimal> BALANCE = Property.create("balance", BigDecimal.class);
    public static final Property<LocalDateTime> CREATE_DATE = Property.create("createDate", LocalDateTime.class);
    public static final Property<String> CURRENCY = Property.create("currency", String.class);
    public static final Property<String> FIRSTNAME = Property.create("firstname", String.class);
    public static final Property<String> LASTNAME = Property.create("lastname", String.class);
    public static final Property<String> NUMBER = Property.create("number", String.class);
    public static final Property<List<Transaction>> TRANSACTIONS = Property.create("transactions", List.class);
    public static final Property<List<Transaction>> TRANSACTIONS1 = Property.create("transactions1", List.class);

    public void setBalance(BigDecimal balance) {
        writeProperty("balance", balance);
    }
    public BigDecimal getBalance() {
        return (BigDecimal)readProperty("balance");
    }

    public void setCreateDate(LocalDateTime createDate) {
        writeProperty("createDate", createDate);
    }
    public LocalDateTime getCreateDate() {
        return (LocalDateTime)readProperty("createDate");
    }

    public void setCurrency(String currency) {
        writeProperty("currency", currency);
    }
    public String getCurrency() {
        return (String)readProperty("currency");
    }

    public void setFirstname(String firstname) {
        writeProperty("firstname", firstname);
    }
    public String getFirstname() {
        return (String)readProperty("firstname");
    }

    public void setLastname(String lastname) {
        writeProperty("lastname", lastname);
    }
    public String getLastname() {
        return (String)readProperty("lastname");
    }

    public void setNumber(String number) {
        writeProperty("number", number);
    }
    public String getNumber() {
        return (String)readProperty("number");
    }

    public void addToTransactions(Transaction obj) {
        addToManyTarget("transactions", obj, true);
    }
    public void removeFromTransactions(Transaction obj) {
        removeToManyTarget("transactions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions() {
        return (List<Transaction>)readProperty("transactions");
    }


    public void addToTransactions1(Transaction obj) {
        addToManyTarget("transactions1", obj, true);
    }
    public void removeFromTransactions1(Transaction obj) {
        removeToManyTarget("transactions1", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions1() {
        return (List<Transaction>)readProperty("transactions1");
    }


}
