package ggc;

import java.io.Serializable;

public class Batch implements Serializable {

    private static final long serialVersionUID = 202110251235L;

    private String _partnerId;
    private String _productId;
    private double _price;
    private int _amount;
    
    public Batch (String productId, String partnerId, double price, int amount) {
        _productId = productId;
        _partnerId = partnerId;
        _price = price;
        _amount = amount;
    }

    public String getPartnerId() {
        return _partnerId;
    }

    public String getProductId() {
        return _productId;
    }

    public double getPrice() {
        return _price;
    }

    public int getRoundedPrice() {
        return (int)Math.round(_price);
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    @Override
    public String toString() {
        return getProductId() + "|" + getPartnerId() + "|" + getRoundedPrice() + "|" + getAmount();
    }
}
