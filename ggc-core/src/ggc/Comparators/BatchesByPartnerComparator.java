package ggc.Comparators;

import java.util.Comparator;
import ggc.Batch;

public class BatchesByPartnerComparator implements Comparator<Batch> {
    
    @Override
        public int compare(Batch b1, Batch b2) {
            if (b1.getProductId().equalsIgnoreCase(b2.getProductId())) {
                if (b1.getRoundedPrice() == b2.getRoundedPrice()) {
                    return b1.getAmount() - b2.getAmount();
                }
                return b1.getRoundedPrice() - b2.getRoundedPrice();
            }
            return b1.getProductId().compareToIgnoreCase(b2.getProductId());
        }  
}