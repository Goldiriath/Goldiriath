package net.goldiriath.plugin.game.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public enum StaticItem {

    MENU_DONE(Material.THIN_GLASS, 0, ChatColor.GREEN + "Done"),
    MENU_CANCEL(Material.THIN_GLASS, 1, ChatColor.RED + "Cancel"),
    //
    MENU_SKILL_UNLEARNED(Material.THIN_GLASS, 2, ChatColor.GRAY + "Unlearned"),
    MENU_SKILL_SWORD(Material.THIN_GLASS, 3, ChatColor.GOLD + "Sword Skills"),
    MENU_SKILL_BOW(Material.THIN_GLASS, 4, ChatColor.GOLD + "Bow Skills"),
    MENU_SKILL_WAND(Material.THIN_GLASS, 5, ChatColor.GOLD + "Wand Skills"),
    MENU_SKILL_KNIFE(Material.THIN_GLASS, 6, ChatColor.GOLD + "Knife Skills"),
    //
    SKILL_BOW_QUICKSHOT(Material.STAINED_GLASS_PANE, 2, ChatColor.GOLD + "Quick shot"),
    SKILL_BOW_POWERSHOT(Material.STAINED_GLASS_PANE, 3, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Powershot"),
    SKILL_BOW_SPREADSHOT(Material.STAINED_GLASS_PANE, 4, ChatColor.GOLD + "Spread shot"),
    SKILL_BOW_PUNCHSHOT(Material.STAINED_GLASS_PANE, 5, ChatColor.GOLD + "Punch shot"),
    SKILL_BOW_BLEEDING_ARROWS(Material.STAINED_GLASS_PANE, 6, ChatColor.GOLD + "Bleeding Arrows"),
    //
    SKILL_SWORD_HOLY_SLAM(Material.STAINED_GLASS_PANE, 2, ChatColor.GOLD + "Holy Slam"),
    SKILL_SWORD_PROTECTIVE_FORMATION(Material.STAINED_GLASS_PANE, 3, ChatColor.GOLD + "Protective Formation"),
    SKILL_SWORD_BLESSING(Material.STAINED_GLASS_PANE, 0, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Blessing"),
    SKILL_SWORD_DIVINE_LIGHT(Material.STAINED_GLASS_PANE, 1, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Divine Light"),
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
