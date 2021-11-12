package ggc;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import ggc.Comparators.BatchesByProductComparator;
import ggc.Comparators.BatchesByPriceComparator;
import ggc.Batch;
import ggc.RecipeIngredient;

public class Product implements Serializable {

    private static final long serialVersionUID = 202110251246L;

    private String _id;
    private double _price;
    private int _amount;
    private ArrayList<Batch> _batchesbyProduct = new ArrayList<>();
    private Notifications _notifications = new Notifications();


    public Product (String id, double price, int amount) {
        _id = id;
        _price = price;
        _amount = amount;
    }

    public String getId() {
        return _id;
    }

    public Double getPrice() {
        return _price;
    }

    public int getAmount() {
        return _amount;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public void setPrice(double price) {
        _price = price;
    }

    public void addAmount(int amount) {
        _amount = _amount + amount;
        if (_amount == amount) {
            getNewStock();
        }
    }

    public double getHighestPrice(double price) {
        if ( _price < price) {
            return price;
        } else {
            return _price;
        }
    }

    public boolean isLowestPrice(double price) {
        for (Batch batch: _batchesbyProduct) {
            if (batch.getPrice() < price) {
                return false;
            }
        }
        return true;
    }

    public int howManyBatches() {
        return _batchesbyProduct.size();
    }

    public void addBatch(Batch batch) {
        if (howManyBatches() != 0) {
            if (isLowestPrice(batch.getPrice())) {
                getNewCheaperBatch(batch.getPrice());
            }
        }
        _batchesbyProduct.add(batch);
    }

    public void registerRecipe(RecipeIngredient recipeIng, boolean last) {}

    public boolean checkComplex() {
        return false;
    }

    public ArrayList<Batch> getBatchesByProductSorted() {
        Collections.sort(_batchesbyProduct, new BatchesByProductComparator());
        return _batchesbyProduct;
    }

    public void addNewInterestedPartner(PartnerObserving partner) {
        _notifications.registerPartnerObserving(partner);
    }

    public void removeInterestedPartner(PartnerObserving partner) {
        _notifications.removePartnerObserving(partner);
    }

    public ArrayList<Batch> getProductBatchesByPrice() {
        Collections.sort(_batchesbyProduct, new BatchesByPriceComparator());
        return _batchesbyProduct;
    }

    public void removeAllZeroBatchesByProduct() {
        for (int i = 0; i < _batchesbyProduct.size(); i++) {
          if (_batchesbyProduct.get(i).getAmount() == 0) {
            _batchesbyProduct.remove(i); }
        }
    }

    public void getNewStock() {
        _notifications.notifyPartnersObserving("NEW", _id, _price);
    }

    public void getNewCheaperBatch(double price) {
        _notifications.notifyPartnersObserving("BARGAIN", _id, price);
    }

    @Override
    public String toString() {
        return getId() + "|" + Math.round(getPrice()) + "|" + getAmount();
    }
}