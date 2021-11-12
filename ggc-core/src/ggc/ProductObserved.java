package ggc;
import java.util.List;
import java.io.Serializable;

public interface ProductObserved {
    public void registerPartnerObserving(PartnerObserving partner);
    public void removePartnerObserving(PartnerObserving partner);
    public void notifyPartnersObserving(String type, String id, double price);
}
