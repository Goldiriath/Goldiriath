package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;
import net.pravian.bukkitlib.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandAction extends AbstractAction {

    private final String command;

    public CommandAction(Goldiriath plugin, String[] args) {
        super(plugin);

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
    public void execute(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

}
