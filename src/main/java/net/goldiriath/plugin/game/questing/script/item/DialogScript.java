package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.game.questing.dialog.Dialog;
import net.goldiriath.plugin.game.questing.dialog.NPCDialogHandler;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import org.bukkit.entity.Player;

public class DialogScript extends ScriptItem {

    private final NPCDialogHandler handler;
    private final String dialogId;

    public DialogScript(Script script, String[] args) {
        super(script);

        handler = plugin.dlm.getHandlers().get(args[1]);
        if (handler == null) {
            throw new ParseException("Could not find NPC dialog handler: " + args[1]);
        }

        // Dialogs may be lazily loaded due to quest/dialog cyclic dependency
        dialogId = args[2];
    }

    @Override
    public void execute(Player player) {
        Dialog dialog = handler.getDialogsMap().get(dialogId);

        if (dialog == null) {
            throw new ParseException("Could not find dialog in NPC dialog handler '" + handler.getId() + "':  " + dialogId);
        }

        dialog.onTrigger(player);
    }

}
