package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(permission = "goldiriath.gitem", source = SourceType.PLAYER)
public class Command_gitem extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length != 1) {
            return false;
        }

        ItemStack stack = plugin.im.getItem(args[0]);
        if (stack == null) {
            msg("That custom item does not exist!", ChatColor.RED);
            return true;
        }

        playerSender.getInventory().addItem(stack);
        msg("Here you go!", ChatColor.GREEN);
        return true;
    }

}
