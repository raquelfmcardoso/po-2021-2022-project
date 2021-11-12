package ggc;


public class AppMethod extends DeliveryMethod {

    private Partner _partner;
    public AppMethod(Partner partner) {
        _partner = partner;
    }
    public String name() {
        return "App";
    }
    public void sendPartnerNotifications(String type, String id, double price) {
        _partner.addNotification(type, id, price);
    }
}
