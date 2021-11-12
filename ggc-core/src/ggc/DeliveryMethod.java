package ggc;

import java.io.Serializable;

public abstract class DeliveryMethod implements Serializable {

    private static final long serialVersionUID = 202111110350L;

    protected DeliveryMethod() {}
    public abstract String name();
    public abstract void sendPartnerNotifications(String type, String id, double price);
}
