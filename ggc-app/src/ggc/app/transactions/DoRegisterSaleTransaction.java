package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.NotEnoughProductException;
import ggc.exceptions.NoSuchPartnerException;
import ggc.exceptions.NoSuchProductException;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Register order.
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerid", Prompt.partnerKey());
    addIntegerField("days", Prompt.paymentDeadline());
    addStringField("productid", Prompt.productKey());
    addIntegerField("amount", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerNewSale(stringField("partnerid"), stringField("productid"), integerField("amount"), integerField("days"));
    } catch (NotEnoughProductException e) {
      throw new UnavailableProductException(e.getId(), e.getRequested(), e.getAvailable());
    } catch (NoSuchPartnerException e) {
      throw new UnknownPartnerKeyException(e.getId());
    } catch (NoSuchProductException e) {
      throw new UnknownProductKeyException(e.getId());
    }
  }
}
