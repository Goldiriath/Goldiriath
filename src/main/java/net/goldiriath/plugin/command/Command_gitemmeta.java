package net.goldiriath.plugin.command;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import net.pravian.bukkitlib.command.BukkitCommand;
import net.pravian.bukkitlib.command.CommandPermissions;
import net.pravian.bukkitlib.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

@CommandPermissions(permission = "goldiriath.gitem", source = SourceType.PLAYER)
public class Command_gitemmeta extends BukkitCommand<Goldiriath> {

    @Override
    protected boolean run(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length != 0) {
            return false;
        }

        ItemStack item = playerSender.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            msg("You don't have an item in your hand!", ChatColor.RED);
            return true;
        }

        boolean cached = true;
        GItemMeta meta = plugin.im.getMeta(item, false);

        if (meta == null) {
            meta = plugin.im.getMeta(item);
            cached = false;
        }

        msg("Item: " + meta.getUniqueId(), ChatColor.AQUA);
        StringBuilder lb = new StringBuilder();
        for (int i = 0; i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
            lb.append("-");
        }
        msg(lb.toString());

        msg("Was cached: " + (cached ? ChatColor.GREEN : ChatColor.RED) + cached, ChatColor.GOLD);
        msg("Name: " + meta.getName(), ChatColor.GOLD);
        msg("Level: " + meta.getLevel(), ChatColor.GOLD);
        msg("Tier: " + meta.getTier(), ChatColor.GOLD);
        msg("Lore:" + StringUtils.join(meta.getLore(), "\n"), ChatColor.GOLD);
        return true;
    }

}
