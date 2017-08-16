package net.goldiriath.plugin.game.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public enum StaticItem {


//    SKILL_BOW_QUICKSHOT(Material.STAINED_GLASS_PANE, 2, ChatColor.GOLD + "Quick shot"),
    SKILL_BOW_POWERSHOT(Material.STAINED_GLASS_PANE, 4, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Powershot"),
    SKILL_BOW_SPREADSHOT(Material.STAINED_GLASS_PANE, 6,ChatColor.AQUA.toString() + ChatColor.ITALIC + "Spread shot"),
    SKILL_BOW_PUNCHSHOT(Material.STAINED_GLASS_PANE, 5, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Punch shot"),
    SKILL_BOW_BLEEDING_ARROWS(Material.STAINED_GLASS_PANE, 7, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Bleeding Arrows"),
    //
    SKILL_SWORD_HOLY_SLAM(Material.STAINED_GLASS_PANE, 0, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Holy Slam"),
    SKILL_SWORD_PROTECTIVE_FORMATION(Material.STAINED_GLASS_PANE, 1, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Protective Formation"),
    SKILL_SWORD_BLESSING(Material.STAINED_GLASS_PANE, 3, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Blessing"),
    SKILL_SWORD_DIVINE_LIGHT(Material.STAINED_GLASS_PANE, 2, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Divine Light"),
    //
    SKILL_KNIFE_STEALTH(Material.STAINED_GLASS_PANE, 12, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Stealth"),
    SKILL_KNIFE_BACK_STAB(Material.STAINED_GLASS_PANE, 13, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Back stab"),
    SKILL_KNIFE_EXECUTE(Material.STAINED_GLASS_PANE, 14, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Execute"),
    SKILL_KNIFE_SHANK(Material.STAINED_GLASS_PANE, 15, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Shank"),
    //
    SKILL_WAND_WARD(Material.STAINED_GLASS_PANE, 8, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Ward"),
    SKILL_WAND_ELEMENTAL_BLAST(Material.STAINED_GLASS_PANE, 9, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Elemental Blast"),
    SKILL_WAND_ELEMENTAL_WAVE(Material.STAINED_GLASS_PANE, 10, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Elemental Wave"),
    SKILL_WAND_DRAW_POWER(Material.STAINED_GLASS_PANE, 11, ChatColor.AQUA.toString() + ChatColor.ITALIC + "Draw Power"),
    SKILL_WAND_WARD_FIRE(Material.STAINED_GLASS, 9, "Fire"),
    SKILL_WAND_WARD_WATER(Material.STAINED_GLASS, 10, "Water"),
    SKILL_WAND_WARD_AIR(Material.STAINED_GLASS, 11, "Air"),
    SKILL_WAND_WARD_EARTH(Material.STAINED_GLASS, 12, "Earth"),
    //
    // Retextured items:
    MENU_DONE(Material.STAINED_GLASS, 0, ChatColor.GREEN + "Done"),
    MENU_CANCEL(Material.STAINED_GLASS, 1, ChatColor.RED + "Cancel"),
    //
    MENU_SKILL_UNLEARNED(Material.STAINED_GLASS, 2, ChatColor.DARK_GRAY + "Unlearned"),
    MENU_SKILL_SWORD(Material.STAINED_GLASS, 3, ChatColor.GOLD + "Sword Skills"),
    MENU_SKILL_BOW(Material.STAINED_GLASS, 4, ChatColor.GOLD + "Bow Skills"),
    MENU_SKILL_WAND(Material.STAINED_GLASS, 5, ChatColor.GOLD + "Wand Skills"),
    MENU_SKILL_KNIFE(Material.STAINED_GLASS, 6, ChatColor.GOLD + "Knife Skills"),
    //
    MENU_BUY_ITEMS(Material.STAINED_GLASS, 7, ChatColor.GOLD + "Buy items"),
    MENU_SELL_ITEMS(Material.STAINED_GLASS, 8, ChatColor.GOLD + "Sell items"),
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
