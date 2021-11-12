package ggc;

public class SMSMethod extends DeliveryMethod {
    private Partner _partner;
    public SMSMethod(Partner partner) {
        _partner = partner;
    }
    public String name() {
        return "SMS";
    }
    public void sendPartnerNotifications(String type, String id, double price) {}
}