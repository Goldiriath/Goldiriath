package me.dirkjan.goldiriath.dialog.script;

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
        player.sendMessage(ChatColor.GREEN + "Note" + ChatColor.RESET + ": " + ChatColor.YELLOW + line);
    }

}
