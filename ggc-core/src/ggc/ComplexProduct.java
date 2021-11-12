package ggc;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

import ggc.RecipeIngredient;

public class ComplexProduct extends Product {

    private double _aggravation = 0.0;
    private String _recipe;
    private Map<String, RecipeIngredient> _recipeIngs = new LinkedHashMap<>();

    public ComplexProduct (String id, double price, int amount, double aggravation, String recipe) {
        super(id, price, amount);
        _aggravation = aggravation;
        _recipe = recipe;
    }

    public double getAggravation() {
        return _aggravation;
    }

    public String getRecipe() {
        return _recipe;
    }

    /*@Override*/
    public List<RecipeIngredient> getRecipeIngredients() {
        getRecipeList();
        ArrayList<RecipeIngredient> list = new ArrayList<>();
        for (RecipeIngredient ingredient: _recipeIngs.values() ) {
            list.add(ingredient);
        }
        return list;
    }

    @Override
    public void registerRecipe(RecipeIngredient recipeIng, boolean last) {
        _recipeIngs.put(recipeIng.getProductId(), recipeIng);
        if (last) {
            _recipe = _recipe + recipeIng.toString();
        } else {_recipe = _recipe + recipeIng.toString() + '#'; }
    }

    public Map<String, RecipeIngredient> getRecipeList() {
        if (_recipeIngs.size() == 0) {
            String[] fields = _recipe.split("[:#]+");
            for (int i = 0; i < fields.length; i++) {
                _recipeIngs.put(fields[i], new RecipeIngredient(fields[i], Integer.parseInt(fields[++i])));
             }
        }
        return _recipeIngs;
    }

    @Override
    public boolean checkComplex() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "|" + getAggravation() + "|" + getRecipe();
    }

}