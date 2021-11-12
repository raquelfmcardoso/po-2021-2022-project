package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.PartnerAlreadyExistsException;
import ggc.app.exceptions.DuplicatePartnerKeyException;

/**
 * Register new partner.
 */
class DoRegisterPartner extends Command<WarehouseManager> {

  DoRegisterPartner(WarehouseManager receiver) {
    super(Label.REGISTER_PARTNER, receiver);
    addStringField("partnerid", Prompt.partnerKey());
    addStringField("name", Prompt.partnerName());
    addStringField("address", Prompt.partnerAddress());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.registerNewPartner(stringField("partnerid"), stringField("name"), stringField("address"));
    } catch (PartnerAlreadyExistsException e) {
      throw new DuplicatePartnerKeyException(e.getId());
    }
  }
}