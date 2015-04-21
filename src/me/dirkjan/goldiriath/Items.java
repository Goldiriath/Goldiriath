package me.dirkjan.goldiriath;

import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Items {

    BAD_SWORD_WOOD1(Material.WOOD_SWORD, "battered wooden sword", 1, "absolute shit"),
    NORMAL_SWORD_WOOD1(Material.WOOD_SWORD, "normal wooden sword", 1, "not special"),
    CRAFTED_SWORD_WOOD1(Material.WOOD_SWORD, "crafted wooden sword", 1, "made by a player"),
    GOOD_SWORD_WOOD1(Material.WOOD_SWORD, "rare wooden sword", 1, "an amazing wood sword"),
    AMAZING_SWORD_WOOD1(Material.WOOD_SWORD, "legendary wooden sword", 1, "the best wooden sword"),
    BAD_BOW_WOOD1(Material.BOW, "used wooden bow", 1, "absolute shit"),
    NORMAL_BOW_WOOD1(Material.BOW, "normal wooden bow", 1, "absolute shit"),
    CRAFTED_BOW_WOOD1(Material.BOW, "crafted wooden bow", 1, "made by a player"),
    GOOD_BOW_WOOD1(Material.BOW, "rare wooden bow", 1, "an amazing wooden bow"),
    AMAZING_BOW_WOOD1(Material.BOW, "legendary wooden bow", 1, "the best wooden bow"),
    BAD_CHESTPLATE_LEATHER1(Material.LEATHER_CHESTPLATE, "used leather chestplate", 1, "absolute shit"),
    NORMAL_CHESTPLATE_LEATHER1(Material.LEATHER_CHESTPLATE, "normal leather chestplate", 1, "not special"),
    CRAFTED_CHESTPLATE_LEATHER1(Material.LEATHER_CHESTPLATE, "crafted leather chestplate", 1, "made by a player"),
    GOOD_CHESTPLATE_LEATHER1(Material.LEATHER_CHESTPLATE, "rare leather chestplate", 1, "an amazing leather chestplate"),
    AMAZING_CHESTPLATE_LEATHER1(Material.LEATHER_CHESTPLATE, "legandary leather chestplate", 1, "the best leather chestplate"),
    BAD_BOOTS_LEATHER1(Material.LEATHER_BOOTS, "used leather boots", 1, "absolute shit"),
    NORMAL_BOOTS_LEATHER1(Material.LEATHER_BOOTS, "normal leather boots", 1, "not special"),
    CRAFTED_BOOTS_LEATHER1(Material.LEATHER_BOOTS, "crafted leather boots", 1, "made by a player"),
    GOOD_BOOTS_LEATHER1(Material.LEATHER_BOOTS, "rare leather boots", 1, "an amazing leather boots"),
    AMAZING_BOOTS_LEATHER1(Material.LEATHER_BOOTS, "legandary leather boots", 1, "the best leather boots"),
    BAD_HELMET_LEATHER1(Material.LEATHER_HELMET, "used leather helmet", 1, "absolute shit"),
    NORMAL_HELMET_LEATHER1(Material.LEATHER_HELMET, "normal leather helmet", 1, "not special"),
    CRAFTED_HELMET_LEATHER1(Material.LEATHER_HELMET, "crafted leather helmet", 1, "made by a player"),
    GOOD_HELMET_LEATHER1(Material.LEATHER_HELMET, "rare leather helmet", 1, "an amazing leather helmet"),
    AMAZING_HELMET_LEATHER1(Material.LEATHER_HELMET, "legandary leather helmet", 1, "the best leather helmet"),
    BAD_LEGGINGS_LEATHER1(Material.LEATHER_LEGGINGS, "used leather leggings", 1, "absolute shit"),
    NORMAL_LEGGINGS_LEATHER1(Material.LEATHER_LEGGINGS, "normal leather leggings", 1, "not special"),
    CRAFTED_LEGGINGS_LEATHER1(Material.LEATHER_LEGGINGS, "crafted leather leggings", 1, "made by a player"),
    GOOD_LEGGINGS_LEATHER1(Material.LEATHER_LEGGINGS, "rare leather leggings", 1, "an amazing leather leggings"),
    AMAZING_LEGGINGS_LEATHER1(Material.LEATHER_LEGGINGS, "legandary leather leggings", 1, "the best leather leggings"),
    BAD_SWORD_WOOD4(Material.WOOD_SWORD, "battered wooden sword", 4, "absolute shit"),
    NORMAL_SWORD_WOOD4(Material.WOOD_SWORD, "normal wooden sword", 4, "not special"),
    CRAFTED_SWORD_WOOD4(Material.WOOD_SWORD, "crafted wooden sword", 4, "made by a player"),
    GOOD_SWORD_WOOD4(Material.WOOD_SWORD, "rare wooden sword", 4, "an amazing wood sword"),
    AMAZING_SWORD_WOOD4(Material.WOOD_SWORD, "legendary wooden sword", 4, "the best wooden sword"),
    BAD_BOW_WOOD4(Material.BOW, "used wooden bow", 4, "absolute shit"),
    NORMAL_BOW_WOOD4(Material.BOW, "normal wooden bow", 4, "absolute shit"),
    CRAFTED_BOW_WOOD4(Material.BOW, "crafted wooden bow", 4, "made by a player"),
    GOOD_BOW_WOOD4(Material.BOW, "rare wooden bow", 4, "an amazing wooden bow"),
    AMAZING_BOW_WOOD4(Material.BOW, "legendary wooden bow", 4, "the best wooden bow"),
    BAD_CHESTPLATE_LEATHER4(Material.LEATHER_CHESTPLATE, "used leather chestplate", 4, "absolute shit"),
    NORMAL_CHESTPLATE_LEATHER4(Material.LEATHER_CHESTPLATE, "normal leather chestplate", 4, "not special"),
    CRAFTED_CHESTPLATE_LEATHER4(Material.LEATHER_CHESTPLATE, "crafted leather chestplate", 4, "made by a player"),
    GOOD_CHESTPLATE_LEATHER4(Material.LEATHER_CHESTPLATE, "rare leather chestplate", 4, "an amazing leather chestplate"),
    AMAZING_CHESTPLATE_LEATHER4(Material.LEATHER_CHESTPLATE, "legandary leather chestplate", 4, "the best leather chestplate"),
    BAD_BOOTS_LEATHER4(Material.LEATHER_BOOTS, "used leather boots", 4, "absolute shit"),
    NORMAL_BOOTS_LEATHER4(Material.LEATHER_BOOTS, "normal leather boots", 4, "not special"),
    CRAFTED_BOOTS_LEATHER4(Material.LEATHER_BOOTS, "crafted leather boots", 4, "made by a player"),
    GOOD_BOOTS_LEATHER4(Material.LEATHER_BOOTS, "rare leather boots", 4, "an amazing leather boots"),
    AMAZING_BOOTS_LEATHER4(Material.LEATHER_BOOTS, "legandary leather boots", 4, "the best leather boots"),
    BAD_HELMET_LEATHER4(Material.LEATHER_HELMET, "used leather helmet", 4, "absolute shit"),
    NORMAL_HELMET_LEATHER4(Material.LEATHER_HELMET, "normal leather helmet", 4, "not special"),
    CRAFTED_HELMET_LEATHER4(Material.LEATHER_HELMET, "crafted leather helmet", 4, "made by a player"),
    GOOD_HELMET_LEATHER4(Material.LEATHER_HELMET, "rare leather helmet", 4, "an amazing leather helmet"),
    AMAZING_HELMET_LEATHER4(Material.LEATHER_HELMET, "legandary leather helmet", 4, "the best leather helmet"),
    BAD_LEGGINGS_LEATHER4(Material.LEATHER_LEGGINGS, "used leather leggings", 4, "absolute shit"),
    NORMAL_LEGGINGS_LEATHER4(Material.LEATHER_LEGGINGS, "normal leather leggings", 4, "not special"),
    CRAFTED_LEGGINGS_LEATHER4(Material.LEATHER_LEGGINGS, "crafted leather leggings", 4, "made by a player"),
    GOOD_LEGGINGS_LEATHER4(Material.LEATHER_LEGGINGS, "rare leather leggings", 4, "an amazing leather leggings"),
    AMAZING_LEGGINGS_LEATHER4(Material.LEATHER_LEGGINGS, "legandary leather leggings", 4, "the best leather leggings"),
    BAD_SWORD_WOOD8(Material.WOOD_SWORD, "battered wooden sword", 8, "absolute shit"),
    NORMAL_SWORD_WOOD8(Material.WOOD_SWORD, "normal wooden sword", 8, "not special"),
    CRAFTED_SWORD_WOOD8(Material.WOOD_SWORD, "crafted wooden sword", 8, "made by a player"),
    GOOD_SWORD_WOOD8(Material.WOOD_SWORD, "rare wooden sword", 8, "an amazing wood sword"),
    AMAZING_SWORD_WOOD8(Material.WOOD_SWORD, "legendary wooden sword", 8, "the best wooden sword"),
    BAD_BOW_WOOD8(Material.BOW, "used wooden bow", 8, "absolute shit"),
    NORMAL_BOW_WOOD8(Material.BOW, "normal wooden bow", 8, "absolute shit"),
    CRAFTED_BOW_WOOD8(Material.BOW, "crafted wooden bow", 8, "made by a player"),
    GOOD_BOW_WOOD8(Material.BOW, "rare wooden bow", 8, "an amazing wooden bow"),
    AMAZING_BOW_WOOD8(Material.BOW, "legendary wooden bow", 8, "the best wooden bow"),
    BAD_CHESTPLATE_LEATHER8(Material.LEATHER_CHESTPLATE, "used leather chestplate", 8, "absolute shit"),
    NORMAL_CHESTPLATE_LEATHER8(Material.LEATHER_CHESTPLATE, "normal leather chestplate", 8, "not special"),
    CRAFTED_CHESTPLATE_LEATHER8(Material.LEATHER_CHESTPLATE, "crafted leather chestplate", 8, "made by a player"),
    GOOD_CHESTPLATE_LEATHER8(Material.LEATHER_CHESTPLATE, "rare leather chestplate", 8, "an amazing leather chestplate"),
    AMAZING_CHESTPLATE_LEATHER8(Material.LEATHER_CHESTPLATE, "legandary leather chestplate", 8, "the best leather chestplate"),
    BAD_BOOTS_LEATHER8(Material.LEATHER_BOOTS, "used leather boots", 8, "absolute shit"),
    NORMAL_BOOTS_LEATHER8(Material.LEATHER_BOOTS, "normal leather boots", 8, "not special"),
    CRAFTED_BOOTS_LEATHER8(Material.LEATHER_BOOTS, "crafted leather boots", 8, "made by a player"),
    GOOD_BOOTS_LEATHER8(Material.LEATHER_BOOTS, "rare leather boots", 8, "an amazing leather boots"),
    AMAZING_BOOTS_LEATHER8(Material.LEATHER_BOOTS, "legandary leather boots", 8, "the best leather boots"),
    BAD_HELMET_LEATHER8(Material.LEATHER_HELMET, "used leather helmet", 8, "absolute shit"),
    NORMAL_HELMET_LEATHER8(Material.LEATHER_HELMET, "normal leather helmet", 8, "not special"),
    CRAFTED_HELMET_LEATHER8(Material.LEATHER_HELMET, "crafted leather helmet", 8, "made by a player"),
    GOOD_HELMET_LEATHER8(Material.LEATHER_HELMET, "rare leather helmet", 8, "an amazing leather helmet"),
    AMAZING_HELMET_LEATHER8(Material.LEATHER_HELMET, "legandary leather helmet", 8, "the best leather helmet"),
    BAD_LEGGINGS_LEATHER8(Material.LEATHER_LEGGINGS, "used leather leggings", 8, "absolute shit"),
    NORMAL_LEGGINGS_LEATHER8(Material.LEATHER_LEGGINGS, "normal leather leggings", 8, "not special"),
    CRAFTED_LEGGINGS_LEATHER8(Material.LEATHER_LEGGINGS, "crafted leather leggings", 8, "made by a player"),
    GOOD_LEGGINGS_LEATHER8(Material.LEATHER_LEGGINGS, "rare leather leggings", 8, "an amazing leather leggings"),
    AMAZING_LEGGINGS_LEATHER8(Material.LEATHER_LEGGINGS, "legandary leather leggings", 8, "the best leather leggings");
    
    

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
