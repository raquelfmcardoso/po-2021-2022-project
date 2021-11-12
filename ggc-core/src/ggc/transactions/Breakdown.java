package ggc.transactions;

public class Breakdown extends Transaction{

    private double _baseValue;
    private double _toPayValue;
    private int _paidDate;
    private String _result;

    public Breakdown(int id, String partnerId, String productId, int amount, double baseValue, double toPayValue, int paidDate, String result) {
        super(id, partnerId, productId, amount);
        _baseValue = baseValue;
        _toPayValue = toPayValue;
        _paidDate = paidDate;
        _result = result;
    }

    public double getBaseValue() {
        return _baseValue;
    }

    public double getToPayValue() {
        return _toPayValue;
    }

    public int getPaidDate() {
        return _paidDate;
    }

    public String getResult() {
        return _result;
    }

    @Override
    public String toString() {
        return "DESAGREGAÇÃO|" + super.toString() + "|" + Math.round(getBaseValue()) + "|" + Math.round(getToPayValue()) + "|" + getPaidDate() + "|" + getResult();
    }

    
}