package ggc;

import java.io.Serializable;

public abstract class PartnerState implements Serializable {
    
    private static final long serialVersionUID = 202111062319L;
    private Partner _partner;
    private String _name;

    public PartnerState(Partner partner, String name) {
        _partner = partner;
        _name = name;
    }

    public Partner getPartner() {
        return _partner;
    }

    public String getName() {
        return _name;
    }
    public abstract void getLatePayment(int date, int limitDate);
    public abstract void getHigherStatus();
    public abstract double getDiscountOrFine(int date, int limitDate, double baseValue, int n);
}