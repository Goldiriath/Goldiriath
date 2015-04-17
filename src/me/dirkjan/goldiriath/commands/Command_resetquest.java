package me.dirkjan.goldiriath.commands;

import java.util.UUID;
import me.dirkjan.goldiriath.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldriathpluginquests.resetquest")
public class Command_resetquest extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }
        final UUID uuid = getPlayer(args[0]).getUniqueId();
        if (plugin.questmap.containsKey(uuid)) {
            plugin.questmap.remove(uuid);
            sender.sendMessage("done");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "uuid has not done any quests");
            return true;
        }

    }

}
