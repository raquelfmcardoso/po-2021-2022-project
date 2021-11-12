package ggc;

import java.io.Serializable;

public class RecipeIngredient implements Serializable {

    private static final long serialVersionUID = 202111082121L;

    private String _productId;
    private int _amount;

    public RecipeIngredient (String productId, int amount) {
        _productId = productId;
        _amount = amount;
    }

    public String getProductId() {
        return _productId;
    }

    public int getAmount() {
        return _amount;
    }

    @Override
    public String toString() {
        return getProductId() + ":" + getAmount();
    }
    
}
