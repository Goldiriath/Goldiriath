package net.goldiriath.plugin.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.goldiriath.plugin.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(permission = "goldiriath.glog", source = SourceType.PLAYER)
public class Command_glog extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

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
