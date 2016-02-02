
package net.goldiriath.plugin.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.util.Util;
import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemRequirement extends AbstractRequirement {

    private final ItemStack stack;
    private final int amount;

    public ItemRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_NEED_ITEMS);

        stack = Util.parseItem(args[1]);
        amount = parseInt(args[2]);
    }

    @Override
    public boolean has(Player player) {
        return player.getInventory().containsAtLeast(stack, amount);
    }

}
