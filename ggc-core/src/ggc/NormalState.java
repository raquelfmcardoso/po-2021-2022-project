package ggc;

public class NormalState extends PartnerState {

    public NormalState(Partner partner) {
        super(partner, "NORMAL");
    }

    public void getLatePayment(int date, int limitDate) {
        getPartner().setPoints(0);
    }
    public void getHigherStatus() {
        if (getPartner().getPoints() > 2000) {
            getPartner().setState(new SelectionState(getPartner())); }
        if (getPartner().getPoints() > 25000) {
            getPartner().setState(new EliteState(getPartner())); }
    }

    public double getDiscountOrFine(int date, int limitDate, double baseValue, int n) {
        if (limitDate - date >= n) {
            return baseValue * 0.9;
        }
        else if (0 <= limitDate - date && limitDate - date < n) {
            return baseValue;
        }
        else if (0 < date - limitDate && date - limitDate <= n) {
            return baseValue *  (1 + (0.05*(date-limitDate)));
        } else {
            return baseValue * (1 + (0.1*(date-limitDate)));
        }
    }
}
