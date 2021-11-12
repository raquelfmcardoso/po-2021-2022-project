package ggc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

import ggc.exceptions.*;
import ggc.Partner;
import ggc.Batch;
import ggc.Product;
import ggc.ComplexProduct;
import ggc.transactions.*;
/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;
  /** _date : integer that represents the current day */
  private int _date = 0;
  /** _availableBalance : double that represents the current available balance of the warehouse*/
  private double _availableBalance = 0;
  /**_accountingBalance : double that represents the current accounting balance of the warehouse */
  private double _accountingBalance = 0;
  /**_idTransaction : int that represents the next number(id) of the next transaction */
  private int _idTransaction = 0;
  /**_partners : treemap that contains the id of the registered partner as key and the partner itself as value*/
  private Map<String, Partner> _partners = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  /**_products : treemap that contains the id of the registered product as key and the product itself as value */
  private Map<String, Product> _products = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  /** _batches : arraylist that contains the batches of the warehouse*/
  private List<Batch> _batches = new ArrayList<>();
  /**_batchesUnderThePrice : arraylist that contains the batches of the warehouse under a given price on getBatchesUnderThePrice */
  private List<Batch> _batchesUnderThePrice = new ArrayList<>();
  /** _transactions : treemap that contains the id of the registered transaction as key and the transaction itself as value */
  private Map<Integer, Transaction> _transactions = new TreeMap<>();

  /**
   * Gets the current day.
   * @return an integer which is the date.
   */
  public int getDate(){
    return _date;
  }

  /**
   * Advances the date based on the amount given.
   * @param amount of days to advance.
   * @throws NoSuchDateException if the amount is 0 or negative.
   */
  public void addDate(int amount) throws NoSuchDateException {
    if (amount <= 0) {
      throw new NoSuchDateException(amount);
    }
    _date += amount;
  }
  
  /**
   * Gets the current available balance.
   * @return a double which is the available balance.
   */
  public double getAvailableBalance() {
    return _availableBalance;
  }
  
  /**
   * Gets the current accounting balance.
   * @return a double which is the accounting balance.
   */
  public double getAccountingBalance() {
    updateAccountingBalance();
    return _accountingBalance;
  }

  /**
   * Updates the accounting balance based on the sales not paid yet according to the date.
   */
  public void updateAccountingBalance() {
    for (Map.Entry<String, Partner> entry : _partners.entrySet()) {
      Partner partner = entry.getValue();
      for (Map.Entry<Integer, Sale> s: partner.getSalesByPartner().entrySet()) {
        Sale sale = s.getValue();
        if (!sale.isPaid(sale.getCurrentDate())) {
          double oldValue = sale.getValueToPay();
          double valueToPay = verifyFinesAndDiscounts(sale.getPartnerId(), sale.getProductId(), sale.getLimitDate(), sale.getBaseValue());
          sale.setValueToPay(valueToPay);
          _transactions.replace(sale.getId(), sale);
          _accountingBalance += (valueToPay-oldValue); }
      }
    } 
  }

  /**
   * Adds a partner to the TreeMap of all the registered partners.
   * @param id of the partner we want to register.
   * @param name of the partner we want to register.
   * @param address of the partner we want to register.
   * @throws PartnerAlreadyExistsException if the id of the partner was already registered.
   */
  public void registerPartner(String id, String name, String address) throws PartnerAlreadyExistsException {
    if (_partners.containsKey(id)) {
      throw new PartnerAlreadyExistsException(id);
    }
    Partner partner = new Partner(id, name, address);
    _partners.put(partner.getId(), partner);
  }

  /**
   * Gets a specific partner from the TreeMap of all the registered partners.
   * @param id of the partner we want to get.
   * @return the partner requested.
   * @throws NoSuchPartnerException if the id of the partner requested is not on the TreeMap.
   */
  public Partner getPartner(String id) throws NoSuchPartnerException {
    Partner partner = _partners.get(id);
    if (partner == null) {
      throw new NoSuchPartnerException(id);
    }
    return partner;
  }

  /**
   * Gets a list with the string of a specific partner from the TreeMap of all the registered partners.
   * If the partner has notifications, their string will also be on the list. 
   * @param id of the partner we want to get.
   * @return a list with the partner requested and their notifications (if they exist)
   * @throws NoSuchPartnerException if the id of the partner requested is not on the TreeMap.
   */
  public List<String> showPartner(String id) throws NoSuchPartnerException {
    Partner partner = getPartner(id);
    List<String> partnerAndNotifications = new ArrayList<>();
    partnerAndNotifications.add(partner.toString());
    int size = partner.getNumberNotifications();
    if (size != 0) {
      for (int i= 0; i < size; i++) {
        partnerAndNotifications.add(partner.notificationToString(i));
      }
    }
    return Collections.unmodifiableList(partnerAndNotifications);
  }

  /**
   * Removes all notifications from a partner.
   * @param id of the partner we want to remove the notifications from.
   * @throws NoSuchPartnerException if the id of the partner requested is not on the TreeMap.
   */
  public void removePartnerNotifications(String id) throws NoSuchPartnerException{
    Partner partner = getPartner(id);
    int size = partner.getNumberNotifications();
    if (size != 0) {
      partner.removeAllNotifications();
    }
  }

  /**
   * Turns on/off a certain product notifications of a specific partner.
   * @param id of the partner we want to switch the notifications.
   * @param id of the product whose notifications will be turned on/off by the partner.
   * @throws NoSuchPartnerException if the id of the partner requested is not on the TreeMap.
   * @throws NoSuchProductException if the id of the product requested is not on the TreeMap.
   */
  public void SwitchOnOffNotifications(String partnerId, String productId) throws NoSuchProductException, NoSuchPartnerException {
    Partner partner = getPartner(partnerId);
    if (_products.containsKey(productId)) {
      Product product = _products.get(productId);
      if (partner.containsProductInInterestedProducts(product)) {
        partner.removeInterestedProduct(product);
        ArrayList<String> notifications = partner.removeNotificationsByProduct(productId);
        partner.setNotifications(notifications);
        product.removeInterestedPartner(partner);
      } else {
        partner.addInterestedProduct(product);
        product.addNewInterestedPartner(partner);
      }
    } else {
      throw new NoSuchProductException(productId);
    }
  }


  /**
   * Gets all the partners registered on the TreeMap.
   * @return a Collection of all the registered partners.
   */
  public Collection<Partner> getAllPartners() {
    return Collections.unmodifiableCollection(_partners.values());
  }

  /**
   * Adds a new batch to the corresponding List of batches associated to the product of the batch.
   * @param productId is the id of the product in the batch.
   * @param partnerId is the id of the partner which is selling the batch.
   * @param price is the monetary value of the batch.
   * @param amount is the quantity of products in the batch.
   * @throws NoSuchPartnerException if the partner which is selling doesn't exist.
   */
  public void registerBatch(String productId, String partnerId, double price, int amount) throws NoSuchPartnerException {
    getPartner(partnerId);
    Batch batch = new Batch(productId, partnerId, price, amount);
    registerProduct(productId, price, amount);
    Product product = _products.get(productId);
    product.addBatch(batch);
    Partner partner = getPartner(partnerId);
    partner.addBatch(batch);
  }

  /**
   * Adds a new batch to the corresponding List of batches associated to the complex product of the batch.
   * @param productId is the id of the product in the batch.
   * @param partnerId is the id of the partner which is selling the batch.
   * @param price is the monetary value of the batch.
   * @param amount is the quantity of products in the batch.
   * @param aggravation is the added value to make the complex product of the batch.
   * @param recipe is the list of ingredients to make the complex product of the batch.
   * @throws NoSuchPartnerException if the partner which is selling doesn't exist.
   */
  public void registerComplexBatch(String productId, String partnerId, double price, int amount, double aggravation, String recipe) throws NoSuchPartnerException {
    getPartner(partnerId);
    Batch batch = new Batch(productId, partnerId, price, amount);
    registerComplexProduct(productId, price, amount, aggravation, recipe);
    Product product = _products.get(productId);
    product.addBatch(batch);
    Partner partner = getPartner(partnerId);
    partner.addBatch(batch);
  }

  /**
   * Gets a list of all the batches of a certain product
   * @param id is the id of the product we want to get all batches.
   * @return a list of batches of only the product specified.
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   */
  public List<Batch> getBatchesByProduct(String id) throws NoSuchProductException{
      Product product = _products.get(id);
      if (product == null) {
        throw new NoSuchProductException(id);
      }
      return Collections.unmodifiableList(product.getBatchesByProductSorted());
  }

  /**
   * Gets a list of all the batches of a certain partner.
   * @param id is the id of the partner we want to get all batches.
   * @return a list of batches that partner has sold to the warehouse.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   */
  public List<Batch> getBatchesByPartner(String id) throws NoSuchPartnerException {
    Partner partner = getPartner(id);
    return Collections.unmodifiableList(partner.getBatchesByPartnerSorted());
  }

  /**
   * Adds all the lists of batches associated to a product into one list with all batches sorted by this order: 
   * product id > partner id > price > amount.
   * @return the list of all the batches in the warehouse.
   */
  public List<Batch> getAllBatches() {
    _batches.clear();
    for (Map.Entry<String, Product> entry: _products.entrySet()) {
      Product product = entry.getValue();
      for (Batch entry2: product.getBatchesByProductSorted()) {
        _batches.add(entry2);
      }
    }
    return Collections.unmodifiableList(_batches);
  }

  /**
   * Gets all the batches under a given price from all the batches in the warehouse.
   * @param price is the value we want to get batches below.
   * @return the list of all the batches in the warehouse below a specific price.
   */
  public List<Batch> getBatchesUnderThePrice(double price) {
    _batchesUnderThePrice.clear();
    getAllBatches();
    for (Batch entry: _batches) {
      if (entry.getPrice() < price) {
        _batchesUnderThePrice.add(entry); 
      }
    }
    return Collections.unmodifiableList(_batchesUnderThePrice);
  }

  /**
   * Adds a new product to the TreeMap of products in the warehouse.
   * @param id of the product.
   * @param price is the highest of all available prices of the product.
   * @param amount is the sum of all the available stock of the product.
   */
  public void registerProduct(String id, double price, int amount) {
    if (_products.containsKey(id)) {
      Product product = _products.get(id);
      product.setPrice(product.getHighestPrice(price));
      product.addAmount(amount);
    } else { Product product = new Product(id, price, amount);
      for (Partner partner: _partners.values()) {
        partner.addInterestedProduct(product);
        product.addNewInterestedPartner(partner);
      }
    _products.put(product.getId(), product); }
  }

  /**
   * Adds a new complex product to the TreeMap of products in the warehouse.
   * @param id of the product.
   * @param price is the highest of all available prices of the product.
   * @param amount is the sum of all the available stock of the product.
   * @param aggravation is the added value to make the complex product.
   * @param recipe is the list of ingredients to make the complex product.
   */
  public void registerComplexProduct(String id, double price, int amount, double aggravation, String recipe) {
    if (_products.containsKey(id)) {
      Product product = _products.get(id);
      product.setPrice(product.getHighestPrice(price));
      product.addAmount(amount);
    } else {Product product = new ComplexProduct(id, price, amount, aggravation, recipe);
      for (Partner partner: _partners.values()) {
        partner.addInterestedProduct(product);
        product.addNewInterestedPartner(partner);
      }
    _products.put(product.getId(), product); }
  }
  
  /**
   * Gets all the products registered.
   * @return a Collection of all the registered products.
   */
  public Collection<Product> getAllProducts() {
    return Collections.unmodifiableCollection(_products.values());
  }
  
  /**
   * Registers the Acquisition of a simple product to the warehouse.
   * @param partnerId is the id of the partner we bought from.
   * @param productId is the id of the product we bought.
   * @param amount is the quantity of product adquired.
   * @param paidValue is the price the warehouse paid.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   */
  public void registerAcquisitionS(String partnerId, String productId, int amount, double paidValue) throws NoSuchPartnerException{
    registerBatch(productId, partnerId, paidValue, amount);
    double totalPaidValue = paidValue * amount;
    Transaction transaction = new Acquisition(_idTransaction, partnerId, productId, amount, totalPaidValue, getDate());
    Acquisition acquisition = new Acquisition(_idTransaction++, partnerId, productId, amount, totalPaidValue, getDate());
    _transactions.put(transaction.getId(), transaction);
    Partner partner = _partners.get(partnerId);
    partner.addAcquisition(transaction.getId(), acquisition);
    _availableBalance -= totalPaidValue;
    _accountingBalance -= totalPaidValue;
    partner.addPurchaseValue(totalPaidValue);
  }

  /**
   * Registers the Acquisition of a complex product to the warehouse.
   * @param partnerId is the id of the partner we bought from.
   * @param productId is the id of the product we bought.
   * @param amount is the quantity of product adquired.
   * @param paidValue is the price the warehouse paid.
   * @param aggravation is the extra value to aggregate this product with it's components.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   */
  public void registerAcquisitionM(String partnerId, String productId, int amount, double paidValue, double aggravation) throws NoSuchPartnerException{
    double totalPaidValue = paidValue * amount;
    String recipe = "";
    registerComplexBatch(productId, partnerId, paidValue, amount, aggravation, recipe);
    Transaction transaction = new Acquisition(_idTransaction, partnerId, productId, amount, totalPaidValue, getDate());
    Acquisition acquisition = new Acquisition(_idTransaction++, partnerId, productId, amount, totalPaidValue, getDate());
    _transactions.put(transaction.getId(), transaction);
    Partner partner = _partners.get(partnerId);
    partner.addAcquisition(transaction.getId(), acquisition);
    _availableBalance -= totalPaidValue;
    _accountingBalance -= totalPaidValue;
    partner.addPurchaseValue(totalPaidValue);
  }

  /**
   * Registers the Sale of a simple or complex product to a partner.
   * @param partnerId is the id of the partner that bought the product.
   * @param productId is the id of the product the partner bought.
   * @param amount is the quantity of product adquired.
   * @param limitDate is the date until the partner starts to lose benefits/discounts/points and starts to get fines from the warehouse.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   * @throws NotEnoughProductException if we don't have enought product to sell and/or we don't have enough of it's components to aggregate it.
   */
  public void registerSale(String partnerId, String productId, int amount, int limitDate) throws NoSuchPartnerException, NoSuchProductException, NotEnoughProductException{
    getPartner(partnerId);
    Product product = getProduct(productId);
    if (product == null) {
      throw new NoSuchProductException(productId);
    }
    if (product.getAmount() < amount) {
      if (confirmComplexityOfProduct(productId)) {
        ComplexProduct complexProduct = (ComplexProduct) product;
        int amountStillNeeded = amount - product.getAmount();
        Map<String, Integer> _productAndAmount = new TreeMap<>();
        Loop(productId, amountStillNeeded, _productAndAmount);
        ArrayList<Batch> _batchesbyCP = product.getProductBatchesByPrice();
        int _quantityCP = product.getAmount();
        double baseValue = 0;
        double baseValueComplexProduct = 0;
        for (int i = 0; i < _batchesbyCP.size(); i++) {
          if (_batchesbyCP.get(i).getAmount() > _quantityCP) {
            baseValueComplexProduct += (_batchesbyCP.get(i).getPrice() * (_quantityCP));
            _batchesbyCP.get(i).setAmount(_batchesbyCP.get(i).getAmount()-_quantityCP);
            i = _batchesbyCP.size();
          } else {
            baseValueComplexProduct += (_batchesbyCP.get(i).getPrice() * _batchesbyCP.get(i).getAmount());
            _quantityCP -= _batchesbyCP.get(i).getAmount();
            _batchesbyCP.get(i).setAmount(0); 
          }
        }

        for (Entry<String, Integer> entry: _productAndAmount.entrySet()) {
          Product product2 = getProduct(entry.getKey());
          product2.setAmount(product2.getAmount() - entry.getValue());
          ArrayList<Batch> _batchesbyP = product2.getProductBatchesByPrice();
          int _quantity = entry.getValue();
          for (int i = 0; i < _batchesbyP.size(); i++) {
            if (_batchesbyP.get(i).getAmount() > _quantity) {
              baseValue += (_batchesbyP.get(i).getPrice() * (_quantity));
              _batchesbyP.get(i).setAmount(_batchesbyP.get(i).getAmount()-_quantity);
              i = _batchesbyP.size();
            } else {
              baseValue += (_batchesbyP.get(i).getPrice() * _batchesbyP.get(i).getAmount());
              _quantity -= _batchesbyP.get(i).getAmount();
              _batchesbyP.get(i).setAmount(0);
            }
          }
          product2.removeAllZeroBatchesByProduct();
        }
        product.setAmount(0);
        product.removeAllZeroBatchesByProduct();
        baseValue *= 1 + complexProduct.getAggravation();
        baseValue += baseValueComplexProduct; 
        double valueToPay = verifyFinesAndDiscounts(partnerId, productId, limitDate, baseValue);
        Transaction transaction = new Sale(_idTransaction, partnerId, productId, amount, baseValue, valueToPay, limitDate);
        Sale sale = new Sale(_idTransaction++, partnerId, productId, amount, baseValue, valueToPay, limitDate);
        Partner partner = _partners.get(partnerId);
        partner.addSale(transaction.getId(), sale);
        _transactions.put(transaction.getId(), transaction);
        partner.addAllSalesValue(baseValue);
        _accountingBalance += valueToPay; 
      } else {
        throw new NotEnoughProductException(productId, amount, product.getAmount());
      }

    } else {
      product.setAmount(product.getAmount() - amount);
      ArrayList<Batch> _batchesbyP = product.getProductBatchesByPrice();
      int _quantity = amount;
      double baseValue = 0;
      for (int i = 0; i < _batchesbyP.size(); i++) {
        if (_batchesbyP.get(i).getAmount() > _quantity) {
          baseValue += (_batchesbyP.get(i).getPrice() * (_quantity));
          _batchesbyP.get(i).setAmount(_batchesbyP.get(i).getAmount()-_quantity);
          i = _batchesbyP.size();
        } else {
          baseValue += (_batchesbyP.get(i).getPrice() * _batchesbyP.get(i).getAmount());
          _quantity -= _batchesbyP.get(i).getAmount();
          _batchesbyP.get(i).setAmount(0); 
        }
      }
      product.removeAllZeroBatchesByProduct();
      double valueToPay = verifyFinesAndDiscounts(partnerId, productId, limitDate, baseValue);
      Transaction transaction = new Sale(_idTransaction, partnerId, productId, amount, baseValue, valueToPay, limitDate);
      Sale sale = new Sale(_idTransaction++, partnerId, productId, amount, baseValue, valueToPay, limitDate);
      Partner partner = _partners.get(partnerId);
      partner.addSale(transaction.getId(), sale);
      _transactions.put(transaction.getId(), transaction);
      partner.addAllSalesValue(baseValue);
      _accountingBalance += valueToPay; 
    }    
  }

  /**
   * Checks the price which the partner is supposed to pay given their status according to the days that have passed since the purchase.
   * @param partnerId is the id of the partner that bought the product.
   * @param productId is the id of the product the partner bought.
   * @param limitDate is the date until the partner starts to lose benefits/discounts/points and starts to get fines from the warehouse.
   * @param baseValue is the price the partner was supposed to pay if there was not a benefits/discounts/points/fines system.
   * @return a double representing the updated price the partner has to pay.
   */
  public double verifyFinesAndDiscounts(String partnerId, String productId, int limitDate, double baseValue) {
    Partner partner = _partners.get(partnerId);
    double valueToPay;
    if (confirmComplexityOfProduct(productId)) {
      int n = 3;
      valueToPay = partner.getState().getDiscountOrFine(getDate(), limitDate, baseValue, n);
    }
    else {
      int n = 5;
      valueToPay = partner.getState().getDiscountOrFine(getDate(), limitDate, baseValue, n);
    }
    return valueToPay;
  }

  /**
   * Receives the product id of an Ingredient and returns it as a Product.
   * @param productId is the id of the ingredient.
   * @return the corresponding product of the ingredient.
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   */
  public Product getProductFromIngredient(String productId) throws NoSuchProductException{
    if (_products.containsKey(productId)) {
      return _products.get(productId);
    }
    throw new NoSuchProductException(productId);
  }

  /**
   * Aux function from registerSale. Receives a complex product and gets its ingredients.
   * @param productId is the id of the complex product.
   * @param amount is the number of complex product needed to aggregate.
   * @param map carries the information needed (ingredient : amount) to aggregate the complex product from registerSale
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   * @throws NotEnoughProductException if we don't have enough components to aggregate the complex product.
   */
  public void Loop(String productId, int amount, Map<String, Integer> map) throws NotEnoughProductException, NoSuchProductException {
    Product product = getProduct(productId); 
    ComplexProduct complexProduct = (ComplexProduct) product;
    int size = complexProduct.getRecipeIngredients().size(); 
    ArrayList<Double> newPrice = new ArrayList<>();
    double price = 0;

    for (int i = 0; i < size; i++) {
      RecipeIngredient ingredient = complexProduct.getRecipeIngredients().get(i); 
      Product ingredientProduct = getProductFromIngredient(ingredient.getProductId());
      int _amount = ingredient.getAmount(); 

      searchForIngredients(ingredient.getProductId(), _amount, map, amount);
      int quantity = map.get(ingredient.getProductId());
      newPrice.add(quantity*ingredientProduct.getPrice());
    }
    for (int i = 0; i < newPrice.size(); i++) {
      price += newPrice.get(i)/amount;
    }
    price *= 1 + complexProduct.getAggravation();
    product.setPrice(product.getHighestPrice(price));
  }

  /**
   * Aux function from registerSale. Receives a ingredient from Loop and if we have enough to aggregate the
   * complex product, it puts it in the map.
   * @param productId is the id of the complex product.
   * @param amountIngredient is the amount need of the ingredient in the recipe.
   * @param map carries the information needed (ingredient : amount) to aggregate the complex product from registerSale
   * @param amountProduct is the amount of times we have to aggregate the complex product.
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   * @throws NotEnoughProductException if we don't have enough ingredient to aggregate the complex product.
   */
  public void searchForIngredients(String productId, int amountIngredient, Map<String, Integer> map, int amountProduct) throws NotEnoughProductException, NoSuchProductException {
    Product product = getProductFromIngredient(productId);
    if (product.getAmount() < (amountIngredient*amountProduct)) {
      if (confirmComplexityOfProduct(product.getId())) {
        int amountStillNeeded = (amountIngredient*amountProduct) - product.getAmount(); 
        if (product.getAmount() > 0) {
          if (map.containsKey(product.getId())) {
            int _amount = map.get(product.getId());
            map.put(product.getId(), product.getAmount()+_amount);
          } else {
            map.put(product.getId(), product.getAmount());
          }
        }
        Loop(product.getId(), amountStillNeeded, map);
      } else {
        throw new NotEnoughProductException(product.getId(), amountIngredient*amountProduct, product.getAmount());
      }
    } else {
      if (map.containsKey(product.getId())) {
        int _amount = map.get(product.getId());
        map.put(product.getId(), (amountIngredient*amountProduct)+_amount);
      } else {
        map.put(product.getId(), (amountIngredient*amountProduct));
      }
    }
  }

  /**
   * Gets a specific product from the TreeMap of all registered products.
   * @param id of the product we want to get.
   * @return the product requested.
   */
  public Product getProduct(String id) {
    Product product = _products.get(id);
    return product;
  }

  /**
   * Returns true if product is complex, false otherwise.
   * @param id of the product we want to check if it's complex.
   * @return boolean true or false, depending if the product is complex or not.
   */
  public boolean confirmComplexityOfProduct(String id) {
    Product product = _products.get(id);
    return product.checkComplex();
  }

  /**
   * Registers an ingredient of a certain product.
   * @param productId we want to register ingredients from.
   * @param id of the ingredient we are registering.
   * @param amount of the ingredient needed in the recipe.
   * @param lastOrNo true if it's the last ingredient of the product's recipe. false otherwise.
   */
  public void registerIngredients(String productId, String id, int amount, boolean lastOrNo) {
    Product product = _products.get(productId);
    RecipeIngredient recipeIng = new RecipeIngredient(id, amount); 
    if (lastOrNo) {
      product.registerRecipe(recipeIng, true);
    } else {
      product.registerRecipe(recipeIng, false);
    } 
  }

  /**
   * Gets a specific transaction from the TreeMap of all registered transactions.
   * @param id of the transaction we want to get.
   * @return the transaction requested.
   * @throws NoSuchTransactionException if the id of the transaction requested is not on the TreeMap.
   */
  public Transaction getTransaction(int id) throws NoSuchTransactionException {
    Transaction transaction = _transactions.get(id);
    if (transaction == null) {
      throw new NoSuchTransactionException(id);
    }
    Partner partner = _partners.get(transaction.getPartnerId());
    if (partner.getSalesByPartner().containsKey(id)) {
      Sale sale = partner.getSalesByPartner().get(id);
      if (!sale.isPaid(sale.getCurrentDate())) {
        double oldValue = sale.getValueToPay();
        double valueToPay = verifyFinesAndDiscounts(sale.getPartnerId(), sale.getProductId(), sale.getLimitDate(), sale.getBaseValue());
        sale.setValueToPay(valueToPay);
        _transactions.replace(id, sale);
        _accountingBalance += (valueToPay-oldValue);
        return sale;
      }
    }
    return transaction;
  }

  /**
   * Receives the payment of a sale that had yet to be paid.
   * @param id of the transaction (sale) we want to get paid.
   * @throws NoSuchTransactionException if the id of the transaction is not on the TreeMap.
   */
  public void receivePayment(int id) throws NoSuchTransactionException {
    Transaction transaction = getTransaction(id);
    Partner partner = _partners.get(transaction.getPartnerId());
    if (partner.getSalesByPartner().containsKey(id)) {
      Sale sale = partner.getSalesByPartner().get(id);
      if (!sale.isPaid(sale.getCurrentDate())) {
        double oldValue = sale.getValueToPay();
        double valueToPay = verifyFinesAndDiscounts(sale.getPartnerId(), sale.getProductId(), sale.getLimitDate(), sale.getBaseValue());
        sale.setValueToPay(valueToPay);
        sale.setPaidDate(getDate());
        sale.setCurrentDate(getDate());
        _accountingBalance += (valueToPay-oldValue);
        _availableBalance += sale.getValueToPay();
        partner.addPaidSalesValue(sale.getValueToPay());
        _transactions.replace(id, sale);
        if (getDate() < sale.getLimitDate()) {
        partner.addPoints(sale.getValueToPay());
        partner.managePartnerState(); } else {
        partner.isLatePayment(getDate(), sale.getLimitDate()); }
      } 
    }
  }

  /**
   * Aux function of registerBreakdown. Receives the attributes of the components after the breakdown and puts them into a string.
   * @param id is the id of the component.
   * @param amount is the quantity of the component being inserted in the warehouse.
   * @param value which is the value of the batch this component is inserted after the breakdown.
   * @param last is true if it is the last component of the product that was broken down.
   * @return the string which has the attributes of the component.
   */
  public String toStringResult(String id, int amount, double value, boolean last) {
    if (last) {
      return id + ":" + amount + ":" + Math.round(value);
    }
    else {
      return id + ":" + amount + ":" + Math.round(value) + "#";
    }
  }

  /**
   * Regists the breakdown and does the breakdown of a complex product for a partner.
   * @param partnerId is the id of the partner who requests the breakdown.
   * @param productId is the id of the product who is going to be breakdown.
   * @param amount of product who is going to be breakdown.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   * @throws NoSuchProductException if the product is not registered on the Treemap.
   * @throws NotEnoughProductException if we don't have enough ingredient to aggregate the complex product.
   */
  public void registerBreakdown(String partnerId, String productId, int amount) throws NoSuchPartnerException, NoSuchProductException, NotEnoughProductException{
    getPartner(partnerId);
    Product p = getProduct(productId);
    if (p == null) {
      throw new NoSuchProductException(productId);
    }
    if (p.getAmount() < amount) {
      throw new NotEnoughProductException(productId, amount, p.getAmount());
    }
    if (confirmComplexityOfProduct(productId)) {
      ComplexProduct product = (ComplexProduct) getProduct(productId);
      product.setAmount(product.getAmount() - amount);
      ArrayList<Batch> _batchesbyP = product.getProductBatchesByPrice();
      int _quantity = amount;
      double baseValueSale = 0;
      for (int i = 0; i < _batchesbyP.size(); i++) {
        if (_batchesbyP.get(i).getAmount() > _quantity) {
          baseValueSale += (_batchesbyP.get(i).getPrice() * (_quantity));
          _batchesbyP.get(i).setAmount(_batchesbyP.get(i).getAmount()-_quantity);
          i = _batchesbyP.size();
        } else {
          baseValueSale += (_batchesbyP.get(i).getPrice() * _batchesbyP.get(i).getAmount());
          _quantity -= _batchesbyP.get(i).getAmount();
          _batchesbyP.get(i).setAmount(0); } }
      product.removeAllZeroBatchesByProduct();
      double totalComponentValue = 0;
      int n = 0;
      String result = "";
      for (Map.Entry<String, RecipeIngredient> entry: product.getRecipeList().entrySet()) {
        n++;
        ArrayList <Batch> _batchbyP = _products.get(entry.getKey()).getProductBatchesByPrice();
        int _totalAmount = entry.getValue().getAmount() * amount;
        double price;
        if (_batchbyP.size() != 0) {
          price = _batchbyP.get(0).getPrice();
        } else { 
          price = _products.get(entry.getKey()).getPrice(); }  
        registerBatch(entry.getKey(), partnerId, price, _totalAmount);
        double componentValue = _totalAmount * price;
        if (n == product.getRecipeList().size()) {
          result += toStringResult(entry.getKey(), _totalAmount, componentValue, true);
        } 
        else { 
        result += toStringResult(entry.getKey(), _totalAmount, componentValue, false); }
        totalComponentValue += componentValue;
      }
      String componentsResult = result;
      double baseValue = baseValueSale - totalComponentValue;
      double toPayValue;
      if (baseValue > 0) 
        toPayValue = baseValue;
      else
        toPayValue = 0;
      Transaction transaction = new Breakdown(_idTransaction, partnerId, productId, amount, baseValue, toPayValue, getDate(), componentsResult);
      Breakdown breakdown = new Breakdown(_idTransaction++, partnerId, productId, amount, baseValue, toPayValue, getDate(), componentsResult);
      Partner partner = _partners.get(partnerId);
      partner.addBreakdown(transaction.getId(), breakdown);
      _transactions.put(transaction.getId(), transaction);
      partner.addPoints(toPayValue);
      partner.managePartnerState();
      _availableBalance += toPayValue; 
      _accountingBalance += toPayValue;} 
  }

  /**
   * Gets all the Acquistions made by a specific partner.
   * @param id of the partner we want to get all acquisitions.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   * @return a collection of all the acquisitions made by the partner.
   */
  public Collection<Acquisition> getPartnerAcquisitions(String id) throws NoSuchPartnerException {
    Partner partner = getPartner(id);
    return Collections.unmodifiableCollection(partner.getAcquisitionsByPartner().values());
  }
  
  /**
   * Gets all the Sales and Breakdowns purchased/requested by a specific partner.
   * @param id of the partner we want to get all sales and breakdowns.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   * @return a collection of all the sales and breakdowns made by the partner.
   */
  public Collection<Transaction> getPartnerSalesAndBreakdowns(String id) throws NoSuchPartnerException {
    updateAccountingBalance();
    Partner partner = getPartner(id);
    Map<Integer, Transaction> _salesAndBreakdowns = new TreeMap<>();
    _salesAndBreakdowns.putAll(partner.getSalesByPartner());
    _salesAndBreakdowns.putAll(partner.getBreakdownsByPartner());
    return Collections.unmodifiableCollection(_salesAndBreakdowns.values());
  }

  /**
   * Gets all the Transactions paid by a specific partner.
   * @param id of the partner we want to get all paid transactions.
   * @throws NoSuchPartnerException if the partner is not registered on the Treemap.
   * @return a collection of all the paid transactions of the partner.
   */
  public Collection <Transaction> getAllPaidTransactions(String id) throws NoSuchPartnerException {
    Partner partner = getPartner(id);
    Map<Integer, Transaction> _paidTransactions = new TreeMap<>();
    for (Map.Entry<Integer, Sale> entry: partner.getSalesByPartner().entrySet()) {
      if (entry.getValue().isPaid(entry.getValue().getCurrentDate())) {
        _paidTransactions.put(entry.getKey(), entry.getValue());
      }
    }
    _paidTransactions.putAll(partner.getBreakdownsByPartner());
    return Collections.unmodifiableCollection(_paidTransactions.values());
  }

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtfile) throws IOException, BadEntryException, PartnerAlreadyExistsException, NoSuchPartnerException /* FIXME maybe other exceptions */ {
    try (BufferedReader in = new BufferedReader(new FileReader(txtfile))) {
      String s;
      while ((s = in.readLine()) != null) {
        String line = new String(s.getBytes(), "UTF-8");
        if (line.charAt(0) == '#')
          continue;

        String[] fields = line.split("\\|");
        switch (fields[0]) {
        case "PARTNER" -> registerPartner(fields[1], fields[2], fields[3]);
        case "BATCH_S" -> registerBatch(fields[1], fields[2], Double.parseDouble(fields[3]), Integer.parseInt(fields[4]));
        case "BATCH_M" -> registerComplexBatch(fields[1], fields[2], Double.parseDouble(fields[3]), Integer.parseInt(fields[4]), Double.parseDouble(fields[5]), fields[6]);
        default -> throw new BadEntryException(fields[0]);
        }
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } catch (PartnerAlreadyExistsException e) {
      e.printStackTrace();
    } catch (BadEntryException e) {
      e.printStackTrace();
    } catch (NoSuchPartnerException e) {
      e.printStackTrace();
    }
  }  
}
