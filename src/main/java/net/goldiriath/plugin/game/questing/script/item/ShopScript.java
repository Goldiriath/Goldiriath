package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.game.questing.dialog.Dialog;
import net.goldiriath.plugin.game.questing.dialog.NPCDialogHandler;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import net.goldiriath.plugin.game.questing.script.ScriptContext;
import net.goldiriath.plugin.game.shop.ShopProfile;
import org.bukkit.entity.Player;

public class ShopScript extends ScriptItem {

    private final ShopProfile profile;
    private final Dialog dialog;

    public ShopScript(Script script, String[] args) {
        super(script);

        if (args.length != 2 && args.length != 3) {
            throw new ParseException("Invalid argument length. Should be either 1 or 2.");
        }

        profile = plugin.sh.getProfiles().get(args[1]);
        if (profile == null) {
            throw new ParseException("Could not find shop profile: " + args[1]);
        }

        if (args.length != 3) {
            dialog = null;
            return;
        }

        if (context().getType() != ScriptContext.ScriptContextType.DIALOG) {
            throw new ParseException("Callback option only available in a dialog context");
        }

        final NPCDialogHandler handler = context().getDialog().getHandler();
        dialog = handler.getDialogsMap().get(args[2]);
        if (dialog == null) {
            throw new ParseException("Could not find dialog: " + args[2]);
        }
    }

    @Override
    public void execute(final Player player) {
        profile.openMenu(player, new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.onTrigger(player);
                }
            }
        });
    }

}
