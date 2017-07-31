package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.data.DataFlags;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandOptions(
        description = "Manage player flags",
        usage = "/<command> <player> <list | <set | add | remove> <flag> <amount>>",
        subPermission = "gflag",
        aliases = "gf,gfl",
        source = SourceType.PLAYER)
public class Command_gflag extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 2) {
            return false;
        }

        Player player = getPlayer(args[0]);
        if (player == null) {
            msg(ChatColor.RED + "Player not found: " + args[0]);
            return true;
        }

        DataFlags flags = plugin.pm.getData(player).getFlags();

        if (args[1].equals("list")) {
            msg(player.getName() + "'s flags:", ChatColor.RED);
            for (String flag : flags.getAll().keySet()) {
                msg("+ " + flag + " -> " + flags.get(flag), ChatColor.GOLD);
            }
            return true;
        }

        if (args.length < 4) {
            return false;
        }

        final String flag = args[2];
        final int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException ex) {
            msg(ChatColor.RED + "Invalid number: " + args[3]);
            return true;
        }

        switch (args[1]) {
            case "set": {
                flags.put(flag, amount);
                break;
            }

            case "add": {
                flags.increment(flag, amount);
                break;
            }
            case "remove": {
                flags.decrement(flag, amount);
                break;
            }

            default: {
                return false;
            }
        }

        msg("Set flag " + flag + " -> " + flags.get(flag));
        return true;
    }

}
