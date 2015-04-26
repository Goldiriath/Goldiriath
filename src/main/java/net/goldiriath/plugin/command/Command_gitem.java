package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandOptions(
        description = "Manage custom items",
        usage = "/<command> <list | get <item>>",
        subPermission = "gitem",
        aliases = "gi",
        source = SourceType.PLAYER)
public class Command_gitem extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

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
                plugin.im.getMeta(stack, true);

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
