package me.dirkjan.goldiriath;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Items {

    BAD_SWORD_WOOD(Material.WOOD_SWORD, "battered wooden sword", 1, "absolute shit"),
    NORMAL_SWORD_WOOD(Material.WOOD_SWORD, "normal wooden sword", 1, "not special"),
    CRAFTED_SWORD_WOOD(Material.WOOD_SWORD, "crafted wooden sword", 1, "made by a player"),
    GOOD_SWORD_WOOD(Material.WOOD_SWORD, "rare wooden sword", 1, "an amazing wood sword"),
    AMAZING_SWORD_WOOD(Material.WOOD_SWORD, "legendary wooden sword", 1, "the best wooden sword"),
    BAD_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "used leather chestplate", 1, "absolute shit"),
    NORMAL_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "normal leather chestplate", 1, "not special"),
    CRAFTED_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "crafted leather chestplate", 1, "made by a player"),
    GOOD_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "rare leather chestplate", 1, "an amazing leather chestplate"),
    AMAZING_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "legandary leather chestplate", 1, "the best leather chestplate");
    
    

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
