package ggc.Comparators;

import java.util.Comparator;
import ggc.Batch;

public class BatchesByProductComparator implements Comparator<Batch> {
    
    @Override
        public int compare(Batch b1, Batch b2) {
            if (b1.getPartnerId().equalsIgnoreCase(b2.getPartnerId())) {
                if (b1.getRoundedPrice() == b2.getRoundedPrice()) {
                    return b1.getAmount() - b2.getAmount();
                }
                return b1.getRoundedPrice() - b2.getRoundedPrice();
            }
            return b1.getPartnerId().compareToIgnoreCase(b2.getPartnerId());
        }  
}
