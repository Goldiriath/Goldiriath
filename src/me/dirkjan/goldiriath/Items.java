package me.dirkjan.goldiriath;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Items {

    ;
    private final ItemStack stack;

    @Deprecated
    private Items(Material type, String name, int lv, String... lore) {
        stack = new ItemStack(type);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("lv" + lv + name);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return stack;
    }
}
