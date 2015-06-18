package me.dirkjan.goldiriath.dialog.script;

import me.dirkjan.goldiriath.dialog.Dialog;
import org.bukkit.entity.Player;

public class ZapScript extends ScriptItem {

    private final Dialog newDialog;

    public ZapScript(Script script, Dialog newDialog) {
        super(script);
        this.newDialog = newDialog;
    }

    @Override
    public void execute(Player player) {

        // Currently: Check requirements before zapping
        newDialog.onTrigger(null, player);

        // Possibility: Ignore dialog requirements
        script.getDialog().getHandler().getManager().getPlugin().pm.getData(player).startDialog(newDialog);

    }

}
