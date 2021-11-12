package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.NoSuchProductException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.NoSuchPartnerException;
import ggc.app.exceptions.UnknownPartnerKeyException;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("partnerid", Prompt.partnerKey());
    addStringField("productid", Prompt.productKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.registerOnOffNotification(stringField("partnerid"), stringField("productid"));
    } catch (NoSuchProductException e) {
      throw new UnknownProductKeyException(e.getId());
    } catch (NoSuchPartnerException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }
}
