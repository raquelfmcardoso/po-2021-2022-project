package ggc.Comparators;

import java.util.Comparator;
import ggc.Batch;

public class BatchesByPriceComparator implements Comparator<Batch> {
    
    @Override
        public int compare(Batch b1, Batch b2) { 
            return b1.getRoundedPrice() - b2.getRoundedPrice();               
    }
}