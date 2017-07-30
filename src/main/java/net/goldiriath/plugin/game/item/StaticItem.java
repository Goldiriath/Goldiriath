package net.goldiriath.plugin.game.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public enum StaticItem {

    MENU_DONE(Material.THIN_GLASS, 0, ChatColor.GREEN + "Done"),
    MENU_SKILL_UNLEARNED(Material.THIN_GLASS, 1, ChatColor.DARK_GRAY + "Unlearned"),
    MENU_SKILL_SWORD(Material.WOOD_SWORD, 0, ChatColor.GOLD + "Sword Skills"),
    MENU_SKILL_BOW(Material.BOW, 0, ChatColor.GOLD + "Bow Skills"),
    //
    SKILL_SWORD_BLESSING(Material.STAINED_GLASS_PANE, 0, ChatColor.GOLD + "Blessing"),
    SKILL_SWORD_DIVINE_LIGHT(Material.STAINED_GLASS_PANE, 1, ChatColor.GOLD + "Divine Light"),
    SKILL_BOW_QUICKSHOT(Material.STAINED_GLASS_PANE, 2, ChatColor.GOLD + "Quickshot"),
    SKILL_BOW_POWERSHOT(Material.STAINED_GLASS_PANE, 3, ChatColor.GOLD + "Powershot"),
    //
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
