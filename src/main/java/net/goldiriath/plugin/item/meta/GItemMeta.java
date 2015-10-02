package net.goldiriath.plugin.item.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.persist.DelegatePersistence;
import net.goldiriath.plugin.persist.Persist;
import net.goldiriath.plugin.player.persist.PersistentStorage;
import org.bukkit.inventory.ItemStack;

public class GItemMeta extends PersistentStorage {

    @Getter
    private final ItemStack stack;

    @Getter
    private final UUID uniqueId;

    @Getter
    @Setter
    @Persist
    private String name = null;

    @Getter
    @Setter
    @Persist
    private int level = 1;

    @Getter
    @Setter
    @Persist
    @DelegatePersistence(ItemTierDelegate.class)
    private ItemTier tier = null;

    @Getter
    @Setter
    @Persist
    private List<String> lore = new ArrayList<>();

    private GItemMeta(ItemStack stack, UUID uniqueId) {
        this.stack = stack;
        this.uniqueId = uniqueId;
    }

    public static GItemMeta createItemMeta(ItemStack stack) {
        UUID uuid = getMetaUuid(stack);
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        return createItemMeta(stack, uuid);
    }

    public static GItemMeta createItemMeta(ItemStack stack, UUID uuid) {

        // Create new itemmeta
        GItemMeta meta = new GItemMeta(stack, uuid);

        org.bukkit.inventory.meta.ItemMeta bMeta = stack.getItemMeta();
        meta.setLore(bMeta.getLore()); // Delegate lore
        bMeta.setLore(Arrays.asList(new String[]{uuid.toString()}));
        stack.setItemMeta(bMeta);

        return meta;
    }

    public static UUID getMetaUuid(ItemStack stack) {

        if (!stack.getItemMeta().hasLore()) {
            return null;
        }

        final List<String> lore = stack.getItemMeta().getLore();

        if (lore == null || lore.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(lore.get(0));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

}
