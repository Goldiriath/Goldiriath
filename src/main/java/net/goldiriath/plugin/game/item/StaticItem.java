package net.goldiriath.plugin.game.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum StaticItem {

    // Retextured items:
    // TODO: Check these
    MENU_DONE(Material.STICK, 0, ChatColor.GREEN + "Done"),
    MENU_CANCEL(Material.STICK, 1, ChatColor.RED + "Cancel"),
    //
    MENU_SKILL_UNLEARNED(Material.STICK, 2, ChatColor.DARK_GRAY + "Unlearned"),
    MENU_SKILL_SWORD(Material.STICK, 3, ChatColor.GOLD + "Sword Skills"),
    MENU_SKILL_BOW(Material.STICK, 4, ChatColor.GOLD + "Bow Skills"),
    MENU_SKILL_WAND(Material.STICK, 5, ChatColor.GOLD + "Wand Skills"),
    MENU_SKILL_KNIFE(Material.STICK, 6, ChatColor.GOLD + "Knife Skills"),
    //
    MENU_BUY_ITEMS(Material.STICK, 7, ChatColor.GOLD + "Buy items"),
    MENU_SELL_ITEMS(Material.STICK, 8, ChatColor.GOLD + "Sell items"),
    //
    SKILL_SWORD_BLESSING(Material.STICK, 9, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Blessing"),
    SKILL_SWORD_DIVINE_LIGHT(Material.STICK, 10, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Divine Light"),
    SKILL_BOW_POWERSHOT(Material.STICK, 11, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Powershot"),
    //
    // Normal items:
    SKILL_BOOK(Material.BOOK, 0, ChatColor.GOLD + "Skill Book");
    //
    private final ItemStack stack;

    private StaticItem(Material mat, int data, String display) {
        this.stack = new ItemStack(mat, 1);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Not a stack with meta: " + mat.toString());
        }

        // Set the data so the resource pack can texture.
        meta.setCustomModelData(data);

        // Set display name
        meta.setDisplayName(display);
        stack.setItemMeta(meta);
    }

    public ItemStack getStack() {
        return stack;
    }

}
