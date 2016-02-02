package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.script.ScriptContext;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutScript extends ScriptItem {

    private final String sound;
    private final String line;

    public OutScript(Script script, String[] args) {
        super(script);
        context(ScriptContext.ScriptContextType.DIALOG);
        this.sound = args[1].equals("_") ? null : args[1].toLowerCase();
        this.line = StringUtils.join(args, " ", 2, args.length);
    }

    @Override
    public void execute(Player player) {
        player.sendMessage(script.getContext().getDialog().getHandler().getNpcName() + ChatColor.RESET + ": " + line);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

}
