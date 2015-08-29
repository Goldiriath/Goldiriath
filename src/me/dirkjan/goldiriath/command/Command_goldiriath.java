package me.dirkjan.goldiriath.command;

import me.dirkjan.goldiriath.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions()
public class Command_goldiriath extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 1) {
            if (!args[0].equals("reload")) {
                return false;
            }

            if (!sender.hasPermission("goldiriath.reload")) {
                return noPerms();
            }

            plugin.onDisable();
            plugin.onEnable();

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
        msg("Created by derpfacedirk and Prozza with " + ChatColor.LIGHT_PURPLE + "<3" + ChatColor.GOLD + ".", ChatColor.GOLD);

        return true;
    }

}
