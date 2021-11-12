package ggc.transactions;

public class Sale extends Transaction {
    
    private double _baseValue;
    private double _valueToPay;
    private int _limitDate;
    private int _paidDate;
    private int _currentDate = 1;

    public Sale(int id, String partnerId, String productId, int amount, double baseValue, double valueToPay, int limitDate) {
        super(id, partnerId, productId, amount);
        _baseValue = baseValue;
        _valueToPay = valueToPay;
        _limitDate = limitDate;
    }

    public Sale(int id, String partnerId, String productId, int amount, double baseValue, double valueToPay, int limitDate, int paidDate, int currentDate) {
        super(id, partnerId, productId, amount);
        _baseValue = baseValue;
        _valueToPay = valueToPay;
        _paidDate = paidDate;
        _limitDate = limitDate;
        _currentDate = currentDate;
    }

    public double getBaseValue() {
        return _baseValue;
    }

    public double getValueToPay() {
        return _valueToPay;
    }

    public int getLimitDate() {
        return _limitDate;
    }

    public int getPaidDate() {
        return _paidDate;
    }

    public int getCurrentDate() {
        return _currentDate;
    }

    public void setCurrentDate(int currentDate) {
        _currentDate = currentDate;
    }

    public void setPaidDate(int paidDate) {
        _paidDate = paidDate;
    }

    public void setValueToPay(double valueToPay) {
        _valueToPay = valueToPay;
    }

    public boolean isPaid(int date) {
        return getPaidDate() == date;
    }

    @Override
    public String toString() {
        if (isPaid(getCurrentDate())) {
            return "VENDA|" + super.toString() + "|" + Math.round(getBaseValue()) + "|" + Math.round(getValueToPay()) + "|" + getLimitDate() + "|" + getPaidDate();
        }
        return "VENDA|" + super.toString() + "|" + Math.round(getBaseValue()) + "|" + Math.round(getValueToPay()) + "|" + getLimitDate();
    }

}