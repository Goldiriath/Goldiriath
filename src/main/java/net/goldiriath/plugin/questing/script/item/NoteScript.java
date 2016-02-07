package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.util.Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NoteScript extends ScriptItem {

    final String line;

    public NoteScript(Script script, String[] args) {
        super(script);

        this.line = StringUtils.join(args, " ", 1, args.length);
    }

    @Override
    public void execute(Player player) {
        String playerLine = Util.prepareLine(line, player);

        player.sendMessage(ChatColor.GREEN + "Note" + ChatColor.RESET + ": " + ChatColor.YELLOW + playerLine);
    }

}
