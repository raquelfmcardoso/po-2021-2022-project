package ggc;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.List;

import ggc.Partner;
import ggc.Batch;
import ggc.Product;
import ggc.ComplexProduct;
import ggc.exceptions.*;
import ggc.transactions.*;

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current store. */
  private String _filename = "";

  /** The warehouse itself. */
  private Warehouse _warehouse = new Warehouse();
  

  public boolean compareFilename() {
    return _filename.equals("");
  }

  public int getcurrentDate() {
    return _warehouse.getDate();
  }

  public void advanceDate(int amount) throws NoSuchDateException {
    _warehouse.addDate(amount);
  }

  public double getcurrentAvailableBalance(){
    return _warehouse.getAvailableBalance();
  }

  public double getcurrentAccountingBalance() {
    return _warehouse.getAccountingBalance();
  }

  public void registerNewPartner(String id, String name, String address) throws PartnerAlreadyExistsException {
    _warehouse.registerPartner(id, name, address);
  }

  public List<String> getSpecificPartner(String id) throws NoSuchPartnerException {
    return _warehouse.showPartner(id);
  }

  public void removeSpecificPartnerNotifications(String id) throws NoSuchPartnerException {
    _warehouse.removePartnerNotifications(id);
  }

  public Collection<Partner> getAllPartners() {
    return _warehouse.getAllPartners();
  }

  public void registerOnOffNotification(String partnerId, String productId) throws NoSuchProductException, NoSuchPartnerException {
    _warehouse.SwitchOnOffNotifications(partnerId, productId);
  }

  public Collection<Product> getAllProducts() {
    return _warehouse.getAllProducts();
  }

  public List<Batch> getAllBatches() {
    return _warehouse.getAllBatches();
  }

  public List<Batch> getBatchesByProduct(String id) throws NoSuchProductException {
    return _warehouse.getBatchesByProduct(id);
  }

  public List<Batch> getBatchesByPartner(String id) throws NoSuchPartnerException {
    return _warehouse.getBatchesByPartner(id);
  }

  public List<Batch> getBatchesUnderThePrice(double price) {
    return _warehouse.getBatchesUnderThePrice(price);
  }

  public void registerNewAcquisitionS(String partnerId, String productId, int amount, double paidValue) throws NoSuchPartnerException {
    _warehouse.registerAcquisitionS(partnerId, productId, amount, paidValue);
  }

  public void registerNewAcquisitionM(String partnerId, String productId, int amount, double paidValue, double aggravation) throws NoSuchPartnerException {
    _warehouse.registerAcquisitionM(partnerId, productId, amount, paidValue, aggravation);
  }

  public void registerNewSale(String partnerId, String productId, int amount, int limitDate) throws NoSuchPartnerException, NoSuchProductException, NotEnoughProductException{
    _warehouse.registerSale(partnerId, productId, amount, limitDate);
  }

  public Product getSpecificProduct(String id) {
    return _warehouse.getProduct(id);
  }

  public void registerIngredientsOfRecipe(String productId, String id, int amount, boolean lastorNo) {
    _warehouse.registerIngredients(productId, id, amount, lastorNo);
  }

  public Transaction getSpecificTransaction(int id) throws NoSuchTransactionException{
    return _warehouse.getTransaction(id);
  }

  public void DoReceivePayment(int id) throws NoSuchTransactionException {
    _warehouse.receivePayment(id);
  }

  public Collection<Acquisition> showPartnerAcquisitions(String id) throws NoSuchPartnerException {
    return _warehouse.getPartnerAcquisitions(id); 
  }

  public void registerNewBreakdown(String partnerId, String productId, int amount) throws NoSuchPartnerException, NoSuchProductException, NotEnoughProductException{
    _warehouse.registerBreakdown(partnerId, productId, amount); 
  }

  public Collection<Transaction> showPartnerSalesAndBreakdowns(String id) throws NoSuchPartnerException {
    return _warehouse.getPartnerSalesAndBreakdowns(id); 
  }

  public Collection <Transaction> showAllPaidTransactions(String id) throws NoSuchPartnerException {
    return _warehouse.getAllPaidTransactions(id);
  }

  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    try {
      ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)));
      oos.writeObject(_warehouse);
      oos.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException {
    try {
      ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
      _warehouse = (Warehouse)ois.readObject();
      _filename = filename;
      ois.close();
    }
    catch(IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(filename);
    }
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
	    _warehouse.importFile(textfile);
    } catch (IOException | BadEntryException | PartnerAlreadyExistsException | NoSuchPartnerException e) {
	    throw new ImportFileException(textfile);
    }
  }
}
