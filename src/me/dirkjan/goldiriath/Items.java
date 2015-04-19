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
    AMAZING_CHESTPLATE_LEATHER(Material.LEATHER_CHESTPLATE, "legandary leather chestplate", 1, "the best leather chestplate"),
    BAD_BOOTS_LEATHER(Material.LEATHER_BOOTS, "used leather boots", 1, "absolute shit"),
    NORMAL_BOOTS_LEATHER(Material.LEATHER_BOOTS, "normal leather boots", 1, "not special"),
    CRAFTED_BOOTS_LEATHER(Material.LEATHER_BOOTS, "crafted leather boots", 1, "made by a player"),
    GOOD_BOOTS_LEATHER(Material.LEATHER_BOOTS, "rare leather boots", 1, "an amazing leather boots"),
    AMAZING_BOOTS_LEATHER(Material.LEATHER_BOOTS, "legandary leather boots", 1, "the best leather boots"),
    BAD_HELMET_LEATHER(Material.LEATHER_HELMET, "used leather helmet", 1, "absolute shit"),
    NORMAL_HELMET_LEATHER(Material.LEATHER_HELMET, "normal leather helmet", 1, "not special"),
    CRAFTED_HELMET_LEATHER(Material.LEATHER_HELMET, "crafted leather helmet", 1, "made by a player"),
    GOOD_HELMET_LEATHER(Material.LEATHER_HELMET, "rare leather helmet", 1, "an amazing leather helmet"),
    AMAZING_HELMET_LEATHER(Material.LEATHER_HELMET, "legandary leather helmet", 1, "the best leather helmet"),
    BAD_LEGGINGS_LEATHER(Material.LEATHER_LEGGINGS, "used leather leggings", 1, "absolute shit"),
    NORMAL_LEGGINGS_LEATHER(Material.LEATHER_LEGGINGS, "normal leather leggings", 1, "not special"),
    CRAFTED_LEGGINGS_LEATHER(Material.LEATHER_LEGGINGS, "crafted leather leggings", 1, "made by a player"),
    GOOD_LEGGINGS_LEATHER(Material.LEATHER_LEGGINGS, "rare leather leggings", 1, "an amazing leather leggings"),
    AMAZING_LEGGINGS_LEATHER(Material.LEATHER_LEGGINGS, "legandary leather leggings", 1, "the best leather leggings");
    
    

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
