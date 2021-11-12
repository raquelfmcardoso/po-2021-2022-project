package ggc;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public interface PartnerObserving {
    public void update(String type, String id, double price);
    public void addNotification(String type, String id, double price);
    public void removeNotification(String notification);
    public ArrayList<String> removeNotificationsByProduct(String productid);
    public void removeAllNotifications();
    public String notificationToString(int i);
}