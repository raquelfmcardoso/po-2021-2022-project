package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.NoSuchPartnerException;
import ggc.app.exceptions.UnknownPartnerKeyException;

import pt.tecnico.uilib.forms.*;
/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partnerid", Prompt.partnerKey());
    addStringField("productid", Prompt.productKey());
    addRealField("price", Prompt.price()); 
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      if (_receiver.getSpecificProduct(stringField("productid")) == null) {
        if (Form.confirm(Prompt.addRecipe())) {
          int numOfComponents = Form.requestInteger(Prompt.numberOfComponents());
          double aggravation = Form.requestReal(Prompt.alpha());
          _receiver.registerNewAcquisitionM(stringField("partnerid"), stringField("productid"), integerField("amount"), realField("price"), aggravation);
          for (int i = 1; i <= numOfComponents; ++i) {
            String id = Form.requestString(Prompt.productKey());
            int amount = Form.requestInteger(Prompt.amount());
            if (i != numOfComponents) {
              _receiver.registerIngredientsOfRecipe(stringField("productid"), id, amount, false);
            } else { _receiver.registerIngredientsOfRecipe(stringField("productid"), id, amount, true); }
          }
        } else {_receiver.registerNewAcquisitionS(stringField("partnerid"), stringField("productid"), integerField("amount"), realField("price"));}
      } else {_receiver.registerNewAcquisitionS(stringField("partnerid"), stringField("productid"), integerField("amount"), realField("price"));}
    } catch (NoSuchPartnerException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }
}
