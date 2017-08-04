package net.goldiriath.plugin.command;

import java.util.Arrays;
import java.util.UUID;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.game.item.meta.ItemTier;
import net.pravian.aero.command.CommandOptions;
import net.pravian.aero.command.SimpleCommand;
import net.pravian.aero.command.SourceType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;

@CommandOptions(
        description = "Shows information about or creates the item meta for an item",
        usage = "/<command> <info | create | delete | set <key> <value...>",
        subPermission = "gitem",
        source = SourceType.PLAYER,
        aliases = "gim,gmeta")
public class Command_gitemmeta extends SimpleCommand<Goldiriath> {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        if (args.length < 1) {
            return false;
        }

        ItemStack stack = playerSender.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            msg("You don't have an item in your hand!", ChatColor.RED);
            return true;
        }

        switch (args[0]) {
            case "info": {
                final UUID uuid = GItemMeta.getMetaUuid(stack);
                final boolean cached = uuid != null && plugin.im.getItemMeta().getMetaCache().containsKey(uuid);
                GItemMeta meta = plugin.im.getMeta(stack, false);
                if (meta == null) {
                    msg("No itemmeta found for this item.", ChatColor.GOLD);
                    msg("Create itemmeta with /gitemmeta create", ChatColor.GOLD);
                    return true;
                }

                msg("Item: " + meta.getUniqueId(), ChatColor.AQUA);
                StringBuilder lb = new StringBuilder();
                for (int i = 0; i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2; i++) {
                    lb.append("-");
                }
                msg(lb.toString());

                msg("Was cached: " + (cached ? ChatColor.GREEN : ChatColor.RED) + cached, ChatColor.GOLD);
                msg("Name: " + meta.getName(), ChatColor.GOLD);
                msg("Level: " + meta.getLevel(), ChatColor.GOLD);
                msg("Tier: " + meta.getTier(), ChatColor.GOLD);
                msg("Lore: " + StringUtils.join(meta.getLore(), "\n"), ChatColor.GOLD);
                return true;
            }

            case "create": {
                GItemMeta meta = plugin.im.getMeta(stack, true);
                msg("Itemmeta created: " + meta.getUniqueId());
                return true;
            }

            case "delete": {
                if (plugin.im.deleteMeta(stack)) {
                    msg("Deleted item's meta data", ChatColor.GREEN);
                } else {
                    msg("Could not delete item's meta data (is there even meta data attached?)", ChatColor.RED);
                }
                return true;
            }

            case "set": {
                if (args.length < 3) {
                    return false;
                }
                GItemMeta meta = plugin.im.getMeta(stack, true);

                // 0: "set"
                // 1: key
                // 2,3,..: value
                final String value = StringUtils.join(args, " ", 2, args.length);

                switch (args[1]) {
                    case "name":
                        meta.setName(value);
                        break;
                    case "level":
                        try {
                            meta.setLevel(Integer.parseInt(value));
                        } catch (NumberFormatException nfex) {
                            msg("Invalid level: " + value, ChatColor.RED);
                            return true;
                        }
                        break;
                    case "lore":
                        meta.setLore(Arrays.asList(value));
                        break;
                    case "tier":
                        try {
                            ItemTier tier = ItemTier.valueOf(value);
                            meta.setTier(tier);
                        } catch (Exception ex) {
                            msg("Unknown tier: " + value, ChatColor.RED);
                            return true;
                        }
                        break;
                    default:
                        msg("Unknown property: " + args[1], ChatColor.RED);
                        return true;
                }

                msg(ChatColor.DARK_GREEN
                        + "Set this item's "
                        + ChatColor.GOLD + args[1]
                        + ChatColor.DARK_GREEN + " to "
                        + ChatColor.GOLD + value
                        + ChatColor.DARK_GREEN + ".");
                return true;
            }

            default: {
                return false;
            }

        } // Switch

    }

}
