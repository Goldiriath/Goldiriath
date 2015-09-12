package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;
import net.goldiriath.plugin.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemAction extends AbstractAction {

    private final ItemStack item;

    public ItemAction(Goldiriath plugin, String[] args) {
        super(plugin);
        item = Util.parseItem(args[1]);
        if (item == null) {
            throw new ParseException("Item '" + args[1] + "' not found");
        }
    }

    @Override
    public void execute(Player player) {
        player.getInventory().addItem(item);
    }

}
