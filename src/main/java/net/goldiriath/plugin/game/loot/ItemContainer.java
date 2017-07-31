package net.goldiriath.plugin.game.loot;

import org.bukkit.inventory.ItemStack;

public class ItemContainer {

    private final ItemStack stack;
    private final Group group;

    public ItemContainer(ItemStack stack) {
        this.stack = stack;
        this.group = null;
    }

    public ItemContainer(Group group) {
        this.stack = null;
        this.group = group;
    }

    public ItemStack getItem() {
        if (stack != null) {
            return stack;
        }
        return group.getRandomItem();
    }

}
