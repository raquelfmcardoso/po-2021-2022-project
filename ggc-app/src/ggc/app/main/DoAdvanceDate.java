package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.NoSuchDateException;
import ggc.app.exceptions.InvalidDateException;

/**
 * Advance current date.
 */
class DoAdvanceDate extends Command<WarehouseManager> {

  DoAdvanceDate(WarehouseManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    addIntegerField("days", Prompt.daysToAdvance());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.advanceDate(integerField("days"));
    } catch (NoSuchDateException e) {
      throw new InvalidDateException(e.getAmount());
    }
  }

}
