package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(permission = "goldiriath.gitem", source = SourceType.PLAYER)
public class Command_gitem extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 1) {
            return false;
        }

        switch (args[0]) {

            case "list": {
                msg("Items: " + ChatColor.GREEN + StringUtils.join(plugin.im.getItemStorage().getItemMap().keySet(), ", "), ChatColor.GOLD);
                return true;
            }

            case "get": {
                ItemStack stack = plugin.im.getItem(args[2]);
                if (stack == null) {
                    msg("That custom item does not exist!", ChatColor.RED);
                    return true;
                }
                // Precache meta
                plugin.im.getMeta(stack);

                playerSender.getInventory().addItem(stack);
                msg("Here you go!", ChatColor.GREEN);
                return true;
            }

            default: {
                return false;
            }
        }
    }

}
