package net.goldiriath.plugin.item;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.FileUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemMetaManager extends AbstractService {

    @Getter
    private final File metaLocation;
    @Getter
    private final Map<UUID, GItemMeta> metaCache = Maps.newHashMap();

    public ItemMetaManager(Goldiriath plugin) {
        super(plugin);
        this.metaLocation = new File(plugin.dataLoadFolder, "items");
    }

    @Override
    protected void onStart() {
        // Make metadata folder
        if (!metaLocation.exists()) {
            metaLocation.mkdirs();
        }

        // Clear Item cache
        metaCache.clear();

        // Attach ProtocolLib adapter
        logger.info("Registering ProtocolLib packet adapter");
        ProtocolManager man = ProtocolLibrary.getProtocolManager();
        man.addPacketListener(new ProtocolLibAdapter(plugin, man));
    }

    @Override
    protected void onStop() {
        metaCache.clear();
    }

    public GItemMeta getMeta(ItemStack stack, boolean create) {

        // Fetch the UUID from the stack
        UUID stackUuid = GItemMeta.getMetaUuid(stack);

        // If there's no UUID present, and we aren't creating meta, return null
        if (stackUuid == null && !create) {
            return null;
        }

        // Lookup the meta from the cache, if it's there
        if (stackUuid != null) {
            GItemMeta cached = metaCache.get(stackUuid);
            if (cached != null) {
                return cached;
            }
        }

        // Obtain the file associated to the UUID
        UUID loadUuid = stackUuid == null ? UUID.randomUUID() : stackUuid;
        File metaFile = getConfigFile(loadUuid);
        boolean configExists = metaFile.exists();

        // If the meta file doesn't exist, we don't want to create new meta, return null
        if (!configExists && !create) {
            return null;
        }

        // If there's a meta referenced, but we can't find the file
        if (!configExists && stackUuid != null) {
            plugin.logger.warning("Could not find referenced item meta for " + stack.getType() + ", generating a new meta!");
        }

        // Create the new item meta, and its config
        GItemMeta meta = GItemMeta.createItemMeta(stack, loadUuid);
        YamlConfig metaConfig = getConfig(metaFile);

        // Load or create it
        if (configExists) {
            // Load the meta
            metaConfig.load();
            meta.loadFrom(metaConfig);
        } else {
            // Write the default meta
            meta.saveTo(metaConfig);
            metaConfig.save();
        }

        // Cache the metadata
        metaCache.put(loadUuid, meta);

        return meta;
    }

    public boolean deleteMeta(ItemStack stack) {
        // Fetch the UUID from the stack
        UUID uuid = GItemMeta.getMetaUuid(stack);
        if (uuid == null) {
            return false;
        }
        metaCache.remove(uuid);
        final File configFile = getConfigFile(uuid);
        return configFile.exists() && configFile.delete();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        cacheMeta(inventory.getContents());
        cacheMeta(inventory.getArmorContents());
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
    public void onItemBreak(PlayerItemBreakEvent event) {
        uncacheMeta(event.getBrokenItem());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Item) {
            uncacheMeta(((Item) event.getEntity()).getItemStack());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory() instanceof PlayerInventory)) {
            uncacheMeta(event.getInventory().getContents());
        }
    }

    private File getConfigFile(UUID uuid) {
        return new File(metaLocation, uuid.toString() + ".yml");
    }

    private YamlConfig getConfig(File file) {
        return new YamlConfig(plugin, file, false);
    }

    private void cacheMeta(ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            if (stack == null) {
                continue;
            }

            UUID stackUuid = GItemMeta.getMetaUuid(stack);
            if (stackUuid == null) {
                continue;
            }

            getMeta(stack, false);
        }
    }

    private void uncacheMeta(ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            if (stack == null) {
                continue;
            }

            UUID stackUuid = GItemMeta.getMetaUuid(stack);
            if (stackUuid == null) {
                continue;
            }

            GItemMeta meta = metaCache.remove(stackUuid);
            if (meta == null) {
                continue;
            }

            logger.info("Saving and removing from cache: " + meta.getUniqueId());

            // Save meta
            YamlConfig metaConfig = getConfig(getConfigFile(stackUuid));
            meta.saveTo(metaConfig);
            metaConfig.save();
        }
    }

}
