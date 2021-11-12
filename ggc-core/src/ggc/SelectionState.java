package ggc;

public class SelectionState extends PartnerState {

    public SelectionState(Partner partner) {
        super(partner, "SELECTION");
    }
    
    public void getLatePayment(int date, int limitDate) {
        getPartner().setState(new NormalState(getPartner()));
        if (date - limitDate > 2) {
            double points = getPartner().getPoints() * 0.10;
            getPartner().setPoints(points);
        }
    }

    public void getHigherStatus() {
        if (getPartner().getPoints() > 25000) {
            getPartner().setState(new EliteState(getPartner())); }
    }

    public double getDiscountOrFine(int date, int limitDate, double baseValue, int n) {
        if (limitDate - date >= n) {
            return baseValue * 0.9;
        }
        else if (0 <= limitDate - date && limitDate - date < n) {
            if (limitDate - date >= 2)
                return baseValue * 0.95;
            return baseValue;
        }
        else if (0 < date - limitDate && date - limitDate <= n) {
            if (date-limitDate > 1)
                return baseValue *  (1 + (0.02*(date-limitDate)));
            return baseValue;
        } else {
            return baseValue * (1 + (0.05*(date-limitDate)));
        }
    }
}
