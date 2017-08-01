package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.shop.Product;
import net.goldiriath.plugin.game.shop.ShopProfile;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandOptions(
        permission = "goldiriath.gshop",
        usage = "/<command> <shop> <info | open>",
        source = SourceType.PLAYER)
public class Command_gshop extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (args.length != 2) {
            return false;
        }

        ShopProfile profile = plugin.sh.getProfiles().get(args[0]);
        if (profile == null) {
            msg(ChatColor.RED + "Could not find shop profile: " + args[0]);
            return true;
        }

        if (args[1].equals("info")) {
            msg(ChatColor.GREEN + "Shop: " + ChatColor.GOLD + profile.getId());
            msg(ChatColor.GREEN + "Exchange rate: " + ChatColor.GOLD + profile.getExchange());
            msg(ChatColor.GREEN + "Products:");
            for (Product product : profile.getAllProducts()) {
                msg(ChatColor.GRAY + " - "
                        + ChatColor.GOLD + product.getAction().name()
                        + " " + product.getDescription()
                        + " for " + product.getPrice() + " Pm"
                );
            }
            return true;
        }

        if (args[1].equals("open")) {
            profile.openMenu(playerSender);
            msg(ChatColor.GREEN + "Opened!");
            return true;
        }

        return false;
    }

}
