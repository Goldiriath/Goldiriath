package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.dialog.Dialog;
import net.goldiriath.plugin.questing.dialog.NPCDialogHandler;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import org.bukkit.entity.Player;

public class DialogScript extends ScriptItem {

    private final Dialog dialog;

    public DialogScript(Script script, String[] args) {
        super(script);

        final NPCDialogHandler handler = plugin.dlm.getHandlers().get(args[1]);
        if (handler == null) {
            throw new ParseException("Could not find NPC dialog handler: " + args[1]);
        }

        dialog = handler.getDialogsMap().get(args[2]);
        if (dialog == null) {
            throw new ParseException("Could not find dialog: " + args[2]);
        }
    }

    @Override
    public void execute(Player player) {
        dialog.onTrigger(player);
    }

}
