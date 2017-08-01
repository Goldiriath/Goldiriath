package net.goldiriath.plugin.game.questing.script.item;

import java.util.Arrays;
import net.goldiriath.plugin.game.questing.dialog.NPCDialogHandler;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import net.goldiriath.plugin.game.questing.script.ScriptContext;
import net.goldiriath.plugin.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutScript extends ScriptItem {

    private final String sound;
    private final String prefix;
    private final String message;

    public OutScript(Script script, String[] args) {
        super(script);

        if (args[1].startsWith("$")) {
            this.sound = args[1].substring(1);
            args = Arrays.copyOfRange(args, 1, args.length);
        } else {
            this.sound = null;
        }

        NPCDialogHandler handler;
        if (context().getType() == ScriptContext.ScriptContextType.DIALOG) {
            handler = script.getContext().getDialog().getHandler();
            this.message = StringUtils.join(args, " ", 1, args.length);
        } else {
            handler = plugin.dlm.getHandlers().get(args[1]);
            if (handler == null) {
                throw new ParseException("Could not find NPC dialog handler: " + args[1]);
            }
            this.message = StringUtils.join(args, " ", 2, args.length);
        }

        this.prefix = handler.getNpcName() + ChatColor.RESET + ": ";
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(Player player) {
        String playerLine = Util.prepareLine(message, player);

        player.sendMessage(prefix + playerLine);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

}
