package me.dirkjan.goldiriath.commands;

import me.dirkjan.goldiriath.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldiriath.mobspawn", source = SourceType.PLAYER)
public class Command_mobspawn extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length == 0) {
            return false;
        }

        if (args[0].equals("dev")) {

            if (args.length == 1) {
                msg("MobSpawn development mode is " + (plugin.ms.isDevMode() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
                return true;
            }

            if (args[1].equals("on")) {
                plugin.ms.setDevMode(true);
            } else {
                plugin.ms.setDevMode(false);
            }

            msg("MobSpawn development mode " + (plugin.ms.isDevMode() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
            return true;
        }

        return false;
    }

}
