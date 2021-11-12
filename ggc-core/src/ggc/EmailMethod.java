package ggc;

public class EmailMethod extends DeliveryMethod {
    private Partner _partner;
    public EmailMethod(Partner partner) {
        _partner = partner;
    }
    public String name() {
        return "Email";
    }
    public void sendPartnerNotifications(String type, String id, double price) {}
}