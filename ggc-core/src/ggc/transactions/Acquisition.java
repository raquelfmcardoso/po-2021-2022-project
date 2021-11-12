package ggc.transactions;

public class Acquisition extends Transaction {
    
    private double _paidValue;
    private int _paidDate;

    public Acquisition(int id, String partnerId, String productId, int amount, double paidValue, int paidDate) {
        super(id, partnerId, productId, amount);
        _paidValue = paidValue;
        _paidDate = paidDate;
    }

    public double getPaidValue() {
        return _paidValue;
    }

    public int getPaidDate() {
        return _paidDate;
    }

    @Override
    public String toString() {
        return "COMPRA|" + super.toString() + "|" + Math.round(getPaidValue()) + "|" + getPaidDate();
    }

}