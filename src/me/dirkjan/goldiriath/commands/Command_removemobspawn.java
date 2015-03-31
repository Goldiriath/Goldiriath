package me.dirkjan.goldiriath.commands;

import static me.dirkjan.goldiriath.Goldriath.plugin;
import me.dirkjan.goldiriath.MobSpawn;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldriathpluginquests.removemobspawn", source = SourceType.PLAYER)
public class Command_removemobspawn {

    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length < 1) {
            return false;
        }

        for (MobSpawn mobspawn : plugin.mobSpawns) {
            if (!args[0].equals(mobspawn.getName())) {
                sender.sendMessage(ChatColor.RED + "that mobspawn doesnt exist");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "done");
            plugin.mobSpawns.remove(mobspawn);
            return true;
        }

        return true;
    }
}
