
package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.shop.Product;
import net.goldiriath.plugin.shop.ShopProfile;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(permission = "goldiriath.gshop", source = SourceType.PLAYER)
public class Command_gshop extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length != 2) {
            return false;
        }

        ShopProfile profile = plugin.sm.getProfiles().get(args[0]);
        if (profile == null) {
            msg(ChatColor.RED + "Could not find shop profile: " + args[0]);
            return true;
        }

        if (args[1].equals("info")) {
            msg(ChatColor.GREEN + "Shop: " + ChatColor.GOLD + profile.getId());
            msg(ChatColor.GREEN + "Exchange rate: " + ChatColor.GOLD + profile.getExchange());
            msg(ChatColor.GREEN + "Products:");
            for (Product product : profile.getProducts()) {
                msg(ChatColor.GRAY + " - " + ChatColor.GOLD + product.getDescription() + " for " + product.getPrice() + " Pm");
            }
            return true;
        }

        if (args[1].equals("open")) {
            profile.getTracker().open(playerSender);
            msg(ChatColor.GREEN + "Opened!");
            return true;
        }

        return false;
    }

}
