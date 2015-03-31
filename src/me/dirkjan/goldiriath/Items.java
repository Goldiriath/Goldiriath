package me.dirkjan.goldiriath;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Items {

    WOODEN_SWORD(Material.WOOD_SWORD, "battered wooden sword", 5, "absolute shit");

    private final ItemStack stack;

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
