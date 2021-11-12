package ggc.transactions;

import java.io.Serializable;

public abstract class Transaction implements Serializable {

    private static final long serialVersionUID = 202111071413L;

    private int _id;
    private String _partnerId;
    private String _productId;
    private int _amount;

    public Transaction (int id, String partnerId, String productId, int amount) {
        _id = id;
        _partnerId = partnerId;
        _productId = productId;
        _amount = amount;
    }

    public int getId() {
        return _id;
    }

    public String getPartnerId() {
        return _partnerId;
    }

    public String getProductId() {
        return _productId;
    } 

    public int getAmount() {
        return _amount;
    }

    @Override
    public String toString() {
        return getId() + "|" + getPartnerId() + "|" + getProductId() + "|" + getAmount();
    }
}