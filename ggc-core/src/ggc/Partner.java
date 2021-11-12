package ggc;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import ggc.Comparators.BatchesByPartnerComparator;
import ggc.Batch;
import ggc.transactions.*;

public class Partner implements Serializable, PartnerObserving {

    private static final long serialVersionUID = 202110241935L;

    private String _id;
    private String _name;
    private String _address;
    private double _points = 0.0;
    private double _purchaseValue = 0.0;
    private double _allSalesValue = 0.0;
    private double _paidSalesValue = 0.0;
    private PartnerState _state = new NormalState(this);
    private ArrayList<Batch> _batchesbyPartner = new ArrayList<>();
    private ArrayList<Product> _interestedProducts = new ArrayList<>();
    private ArrayList<String> _notifications = new ArrayList<>();
    private DeliveryMethod _deliveryMethod = new AppMethod(this);
    private Map<Integer, Sale> _sales = new TreeMap<>();
    private Map<Integer, Acquisition> _acquisitions = new TreeMap<>();
    private Map<Integer, Breakdown> _breakdowns = new TreeMap<>();

    public Partner(String id, String name, String address) {
        _id = id;
        _name = name;
        _address = address;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        _deliveryMethod = deliveryMethod;
    }

    public String getDeliveryMethod() {
        return _deliveryMethod.name();
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getAddress() {
        return _address;
    }

    public double getPoints() {
        return _points;
    }

    public void addBatch(Batch batch) {        
        _batchesbyPartner.add(batch);
    }
    public void addPaidSalesValue (double paidSalesValue) {
        _paidSalesValue += paidSalesValue;
    }

    public void addPurchaseValue(double purchaseValue) {
        _purchaseValue += purchaseValue;
    }

    public void addAllSalesValue(double allSalesValue) {
        _allSalesValue += allSalesValue;
    }

    public ArrayList<Batch> getBatchesByPartnerSorted() {
        Collections.sort(_batchesbyPartner, new BatchesByPartnerComparator());
        return _batchesbyPartner;
    }

    public void addAcquisition(int id, Acquisition acquisition) {
        _acquisitions.put(id, acquisition);
    }

    public void addSale(int id, Sale sale) {
        _sales.put(id, sale);
    }

    public void addBreakdown(int id, Breakdown breakdown) {
        _breakdowns.put(id, breakdown);
    }

    public Map<Integer, Acquisition> getAcquisitionsByPartner() {
        return _acquisitions;
    }

    public Map<Integer, Sale> getSalesByPartner() {
        return _sales;
    }

    public Map<Integer, Breakdown> getBreakdownsByPartner() {
        return _breakdowns;
    }

    public void setState(PartnerState state) {
        _state = state;
    }

    public PartnerState getState() {
        return _state;
    }

    public void isLatePayment(int date, int limitDate) {
        _state.getLatePayment(date, limitDate);
    }
    public void isHigherStatus() {
        _state.getHigherStatus();
    }

    public void managePartnerState() {
        if (_state.getName() == "NORMAL") {
            if (_points > 2000){
                isHigherStatus();
            }
        }
        if (_state.getName() == "SELECTION") {
            if (_points > 25000) {
                isHigherStatus();
            }
        }
    }

    public void addInterestedProduct(Product product) {
        _interestedProducts.add(product);
    }

    public void removeInterestedProduct(Product product) {
        int i = _interestedProducts.indexOf(product);
        _interestedProducts.remove(i);
    }

    public boolean containsProductInInterestedProducts(Product product) {
        if (_interestedProducts.size() == 0) {
            return false;
        }
        return _interestedProducts.contains(product);
    }

    public void update(String type, String id, double price) {
        _deliveryMethod.sendPartnerNotifications(type, id, price);
    }

    public void addNotification(String type, String id, double price) {
        _notifications.add(type + "|" + id + "|" + Math.round(price));
    }

    public void removeNotification(String notification) {
        int i = _notifications.indexOf(notification);
        if (i >= 0) {
            _notifications.remove(i);
        }
    }

    public void setNotifications(ArrayList<String> notifications) {
        _notifications.clear();
        for (String notification : notifications) {
            _notifications.add(notification);
        }
    }

    public ArrayList<String> removeNotificationsByProduct(String productid) {
        ArrayList<String> notifications = new ArrayList<>();
        for (String notification : _notifications) {
            if (!notification.contains(productid)) {
                notifications.add(notification);
            }
        }
        return notifications;
    }

    public void removeAllNotifications() {
        _notifications.clear();
    }

    public String notificationToString(int i) {
        return _notifications.get(i);
    }
    
    public void addPoints(double paidValue) {
        _points += (paidValue * 10);
    }

    public void setPoints(double points) {
        _points = points;
    }

    public int getNumberNotifications() {
        return _notifications.size();
    }

    @Override
    public String toString() {
        return getId() + "|" + getName() + "|" + getAddress() + "|" + _state.getName() + "|" + Math.round(_points) + "|" + Math.round(_purchaseValue) + "|" + Math.round(_allSalesValue) + "|" + Math.round(_paidSalesValue);
    }
}