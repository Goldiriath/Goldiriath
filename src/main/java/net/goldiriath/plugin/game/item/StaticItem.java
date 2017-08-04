package net.goldiriath.plugin.game.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public enum StaticItem {

    // Retextured items:
    MENU_DONE(Material.STAINED_GLASS, 0, ChatColor.GREEN + "Done"),
    MENU_CANCEL(Material.STAINED_GLASS, 1, ChatColor.RED + "Cancel"),
    //
    MENU_SKILL_UNLEARNED(Material.STAINED_GLASS, 2, ChatColor.DARK_GRAY + "Unlearned"),
    MENU_SKILL_SWORD(Material.STAINED_GLASS, 3, ChatColor.GOLD + "Sword Skills"),
    MENU_SKILL_BOW(Material.STAINED_GLASS, 4, ChatColor.GOLD + "Bow Skills"),
    MENU_SKILL_WAND(Material.THIN_GLASS, 5, ChatColor.GOLD + "Wand Skills"),
    MENU_SKILL_KNIFE(Material.THIN_GLASS, 6, ChatColor.GOLD + "Knife Skills"),
    //
    MENU_BUY_ITEMS(Material.STAINED_GLASS, 7, ChatColor.GOLD + "Buy items"),
    MENU_SELL_ITEMS(Material.STAINED_GLASS, 8, ChatColor.GOLD + "Sell items"),
    //
    SKILL_SWORD_BLESSING(Material.STAINED_GLASS_PANE, 2, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Blessing"),
    SKILL_SWORD_DIVINE_LIGHT(Material.STAINED_GLASS_PANE, 3, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Divine Light"),
    SKILL_BOW_POWERSHOT(Material.STAINED_GLASS_PANE, 4, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Powershot"),
    //
    // Normal items:
    SKILL_BOOK(Material.BOOK, 0, ChatColor.GOLD + "Skill Book");
    //
    private final ItemStack stack;

    private StaticItem(Material mat, int data, String display) {
        MaterialData matData = new ItemStack(mat, 1).getData();
        matData.setData((byte) data);
        this.stack = matData.toItemStack(1);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(display);
        stack.setItemMeta(meta);
    }

    public ItemStack getStack() {
        return stack;
    }

}
