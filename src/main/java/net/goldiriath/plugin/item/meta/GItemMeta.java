package net.goldiriath.plugin.item.meta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.util.persist.DelegatePersistence;
import net.goldiriath.plugin.util.persist.Persist;
import net.goldiriath.plugin.util.persist.PersistentStorage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    private List<String> lore = null;

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

        // Set the UUID lore reference
        org.bukkit.inventory.meta.ItemMeta bMeta = stack.getItemMeta();
        bMeta.setLore(Arrays.asList(new String[]{uuid.toString()}));

        // Update the stack's meta
        stack.setItemMeta(bMeta);
        return meta;
    }

    public static UUID getMetaUuid(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return null;
        }

        final List<String> lore = meta.getLore();

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
