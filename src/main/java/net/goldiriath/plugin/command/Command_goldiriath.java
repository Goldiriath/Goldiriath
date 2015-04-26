package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        description = "Shows information about this plugin",
        usage = "/<command> [reload]",
        aliases = "go")
public class Command_goldiriath extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 1) {
            if (!args[0].equals("reload")) {
                return false;
            }

            if (!sender.hasPermission("goldiriath.reload")) {
                return noPerms();
            }
            
            plugin.disable();
            plugin.enable();

            final String message = String.format("%s v%s-%s reloaded.",
                    plugin.getName(),
                    plugin.getVersion(),
                    Goldiriath.buildVersion);

            msg(message);
            plugin.logger.info(message);
            return true;
        }

        msg(Goldiriath.name + " pre-alpha", ChatColor.GOLD);
        msg(String.format("Version "
                + ChatColor.BLUE + "%s-%s" + ChatColor.GOLD + ", built "
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + ".",
                plugin.getVersion(),
                Goldiriath.buildVersion,
                Goldiriath.buildDate), ChatColor.GOLD);
        msg("Running on Minecraft " + Bukkit.getBukkitVersion() + ".", ChatColor.GOLD);
        msg("Created by Prozza and derpfacedirk with " + ChatColor.LIGHT_PURPLE + "<3" + ChatColor.GOLD + ".", ChatColor.GOLD);
        return true;
    }

}
