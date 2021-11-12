package ggc;

public class EliteState extends PartnerState {

    public EliteState(Partner partner) {
        super(partner, "ELITE");
    }

    public void getLatePayment(int date, int limitDate) {
        getPartner().setState(new SelectionState(getPartner()));
        if (date - limitDate > 15) {
            double points = getPartner().getPoints() * 0.25;
            getPartner().setPoints(points);
        }
    }
    public void getHigherStatus() {}

    public double getDiscountOrFine(int date, int limitDate, double baseValue, int n) {
        if (limitDate - date >= n) {
            return baseValue * 0.9;
        }
        else if (0 <= limitDate - date && limitDate - date < n) {
            return baseValue * 0.9;
        }
        else if (0 < date - limitDate && date - limitDate <= n) {
            return baseValue * 0.95;
        } else {
            return baseValue;
        }
    }
}