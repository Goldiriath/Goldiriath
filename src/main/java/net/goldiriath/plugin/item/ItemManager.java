package net.goldiriath.plugin.item;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.ItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.FileUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemManager extends AbstractService {

    @Getter
    private final File metaLocation;
    @Getter
    private final ItemStorage storage;
    @Getter
    private final Map<ItemStack, ItemMeta> metaCache = Maps.newHashMap();

    public ItemManager(Goldiriath plugin) {
        super(plugin);
        this.metaLocation = new File(new File(FileUtils.getPluginDataFolder(plugin), "data"), "items");
        this.storage = new ItemStorage(plugin, this);
    }

    @Override
    protected void onStart() {

        // Make metadata folder
        if (!metaLocation.exists()) {
            metaLocation.mkdirs();
        }

        storage.start();
    }

    @Override
    protected void onStop() {
        storage.stop();
    }

    //
    public ItemStack getItem(String id) {
        return storage.getItemMap().get(id);
    }

    public ItemMeta getMeta(ItemStack stack) {
        return getMeta(stack, true);
    }

    public ItemMeta getMeta(ItemStack stack, boolean create) {
        ItemMeta meta = metaCache.get(stack);

        if (meta != null || !create) {
            return meta;
        }

        // Create metadata and attach it to the stack
        meta = ItemMeta.createItemMeta(stack);

        // Load the meta or write the default meta
        YamlConfig metaConfig = getConfig(meta);
        if (metaConfig.exists()) {
            meta.loadFrom(metaConfig); // Load
        } else {
            meta.saveTo(metaConfig); // Write default
            metaConfig.save();
        }

        // Cache the metadata
        metaCache.put(stack, meta);

        return meta;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        uncacheMeta(inventory.getArmorContents());
        uncacheMeta(inventory.getContents());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDespawn(ItemDespawnEvent event) {
        uncacheMeta(event.getEntity().getItemStack());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDestroy(PlayerItemBreakEvent event) {
        uncacheMeta(event.getBrokenItem());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Item) {
            uncacheMeta(((Item) event.getEntity()).getItemStack());
        }
    }

    private YamlConfig getConfig(ItemMeta meta) {
        return new YamlConfig(plugin, new File(metaLocation, meta.getUniqueId().toString()), false);
    }

    private void uncacheMeta(ItemStack... stacks) {
        for (ItemStack stack : stacks) {

            ItemMeta meta = metaCache.remove(stack);

            if (meta == null) {
                continue;
            }

            // Save meta
            YamlConfig metaConfig = getConfig(meta);
            meta.saveTo(metaConfig);
            metaConfig.save();
        }
    }

}
