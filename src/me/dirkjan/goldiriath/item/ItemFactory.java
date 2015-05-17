package me.dirkjan.goldiriath.item;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {

    private final Material type;
    private byte data = 0;
    private String name = null;
    private String[] lore = {};
    private ItemTier tier = null;
    private int level = -1;

    private ItemFactory(Material type) {
        this.type = Preconditions.checkNotNull(type);
    }

    public static ItemFactory builder(Material type) {
        return new ItemFactory(type);
    }

    @Deprecated // Unimplemented, post-alpha
    public ItemFactory withName(String name) {
        this.name = name;
        return this;
    }

    public ItemFactory withLore(String... lore) {
        this.lore = lore;
        return this;
    }

    public ItemFactory withTier(ItemTier tier) {
        this.tier = tier;
        return this;
    }

    public ItemFactory withLevel(int level) {
        this.level = level;
        return this;
    }

    public ItemFactory withData(byte data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("deprecation") // data byte
    public ItemStack build() {
        final ItemStack item = new ItemStack(type, 1);

        // Meta
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name != null ? name : tier.getAdjective(type) + " " + type.toString().toLowerCase().replace('_', ' '));

        final List<String> loreList = new ArrayList<>();
        loreList.add("Level " + level);
        loreList.addAll(Arrays.asList(lore));
        meta.setLore(loreList);

        // Data
        item.getData().setData(data);

        return item;
    }
}
