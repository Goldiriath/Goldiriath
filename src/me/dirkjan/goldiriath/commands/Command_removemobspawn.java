package me.dirkjan.goldiriath.commands;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.MobSpawner.MobSpawn;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldriathpluginquests.removemobspawn", source = SourceType.PLAYER)
public class Command_removemobspawn extends BukkitCommand<Goldiriath> {

    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length < 1) {
            return false;
        }

        for (MobSpawn mobspawn : plugin.ms.getSpawns()) {
            if (!args[0].equals(mobspawn.getName())) {
                sender.sendMessage(ChatColor.RED + "that mobspawn doesnt exist");
                return true;
            }
            sender.sendMessage(ChatColor.GREEN + "done");
            plugin.ms.remove(mobspawn);
            return true;
        }

        return true;
    }
}
