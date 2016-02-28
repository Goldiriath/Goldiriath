package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.util.Util;
import net.pravian.bukkitlib.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandScript extends ScriptItem {

    private final String command;

    public CommandScript(Script script, String[] args) {
        super(script);

        if (args.length == 1) {
            throw new ParseException("Need a command to execute!");
        }

        String tempCommand = StringUtils.join(args, " ", 1, args.length);
        if (tempCommand.startsWith("/")) {
            tempCommand = tempCommand.substring(1);
        }

        command = tempCommand;
    }

    @Override
    public void execute(Player player) { // TODO: Execute as player instead?
        final String playerCommand = Util.prepareLine(command, player);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
    }

}
