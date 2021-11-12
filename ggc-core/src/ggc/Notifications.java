package ggc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class Notifications implements ProductObserved, Serializable {

    private static final long serialVersionUID = 202111062320L;

    private ArrayList<PartnerObserving> _observers = new ArrayList<>();
    
    public Notifications() {}

    public void registerPartnerObserving(PartnerObserving partner) {
        _observers.add(partner);
    }

    public void removePartnerObserving(PartnerObserving partner) {
        int i = _observers.indexOf(partner);
        if (i >= 0) { 
            _observers.remove(i);
        }
    }

    public void notifyPartnersObserving(String type, String id, double price) {
        for (PartnerObserving observer: _observers) {
            observer.update(type, id, price);
        }
    }
}
