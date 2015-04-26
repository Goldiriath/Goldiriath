package net.goldiriath.plugin.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.goldiriath.plugin.Goldiriath;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandOptions(
        description = "Manage player logging",
        usage = "/<command> <list | add | remove>",
        subPermission = "glog",
        source = SourceType.PLAYER,
        aliases = "gl")
public class Command_glog extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length != 1) {
            return false;
        }

        switch (args[0]) {
            case "list": {
                final List<String> playerNames = Lists.newArrayList();
                for (Player player : plugin.loggerPlayers.getPlayerMembers()) {
                    playerNames.add(player.getName());
                }

                msg("Log listeners: " + StringUtils.join(playerNames, ", "), ChatColor.GOLD);
                return true;
            }

            case "add": {
                plugin.loggerPlayers.add(playerSender);
                msg("Added to log listeners", ChatColor.GREEN);
                return true;
            }

            case "remove": {
                plugin.loggerPlayers.remove(playerSender);
                msg("Removed from log listeners", ChatColor.GREEN);
                return true;
            }

            default: {
                return false;
            }
        }

    }

}
