package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.NoSuchPartnerException;
import ggc.app.exceptions.UnknownPartnerKeyException;
/**
 * Show partner.
 */
class DoShowPartner extends Command<WarehouseManager> {

  DoShowPartner(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER, receiver);
    addStringField("partnerid", Prompt.partnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _display.popup(_receiver.getSpecificPartner(stringField("partnerid")));
      _receiver.removeSpecificPartnerNotifications(stringField("partnerid"));
      
    } catch (NoSuchPartnerException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }
}