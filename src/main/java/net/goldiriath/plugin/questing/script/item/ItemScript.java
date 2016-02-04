package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemScript extends ScriptItem {

    private final boolean add;
    private final ItemStack item;

    public ItemScript(Script script, String[] args) {
        super(script);

        add = args[1].equals("add");
        if (!add && !args[1].equals("remove")) {
            throw new ParseException("Invalid operation '" + args[1] + "'. Either 'add' or 'remove'");
        }

        ItemStack temp = Util.parseItem(args[2]);
        if (temp == null) {
            throw new ParseException("Item '" + args[2] + "' not found");
        }
        item = temp.clone();

        int amount = parseInt(args[3]);
        item.setAmount(amount);
    }

    @Override
    public void execute(Player player) {
        if (add) {
            player.getInventory().addItem(item);
        } else {
            player.getInventory().removeItem(item);
        }

    }

}
