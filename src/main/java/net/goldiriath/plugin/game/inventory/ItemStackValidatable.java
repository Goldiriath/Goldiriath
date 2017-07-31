package net.goldiriath.plugin.game.inventory;

import org.bukkit.inventory.ItemStack;

public interface ItemStackValidatable {

    public boolean validate(ItemStack stack);

}
