package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MeScript extends ScriptItem {

    private final String message;

    public MeScript(Script script, String[] args) {
        super(script);

        this.message = StringUtils.join(args, " ", 1, args.length);
    }

    @Override
    public void execute(Player player) {
        String playerLine = Util.prepareLine(message, player);

        player.sendMessage(ChatColor.GREEN + "Me" + ChatColor.RESET + ": " + playerLine);
    }

}
