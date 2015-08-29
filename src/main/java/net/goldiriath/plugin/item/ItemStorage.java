package net.goldiriath.plugin.item;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.ItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.ChatUtils;
import net.pravian.bukkitlib.util.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ItemStorage extends AbstractService {

    private final YamlConfig config;
    private final File metaLocation;
    private final Map<String, ItemStack> customItems = new HashMap<>();
    private final Map<ItemStack, ItemMeta> metaCache = new HashMap<>();

    public ItemStorage(Goldiriath plugin) {
        super(plugin);
        this.config = new YamlConfig(plugin, "items.yml");
        this.metaLocation = new File(new File(FileUtils.getPluginDataFolder(plugin), "data"), "items");
    }

    @Override
    protected void onStart() {

        // Load config
        config.load();

        // Make metadata folder
        if (!metaLocation.exists()) {
            metaLocation.mkdirs();
        }

        // Load custom items
        customItems.clear();
        metaCache.clear();
        for (String id : config.getKeys(false)) {

            // ID
            if (!config.isConfigurationSection(id)) {
                logger.warning("Skipping item: " + id + ". Incorrect format!");
                continue;
            }
            ConfigurationSection section = config.getConfigurationSection(id);

            // Type
            String typeString = section.getString("type", null);
            if (typeString == null) {
                logger.warning("Skipping item: " + id + ". Missing type!");
                continue;
            }
            Material type = Material.matchMaterial(typeString);
            if (type == null) {
                logger.warning("Skipping item: " + id + ". Could not determine type!");
                continue;
            }

            // Data
            byte data = Integer.valueOf(section.getInt("data", 0)).byteValue();
            if (data < 0) {
                logger.warning("Skipping item: " + id + ". Invalid data: " + data + "!");
                continue;
            }

            // Create itemstack
            final ItemStack stack = new ItemStack(type, 1);

            // Create and load metadata
            final ItemMeta meta = ItemMeta.createItemMeta(stack, UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)));
            metaCache.put(stack, meta);
            ConfigurationSection metaSection = section.getConfigurationSection("meta");
            if (metaSection != null) {
                meta.loadFrom(section);
            }

            // Bukkit metadata
            final org.bukkit.inventory.meta.ItemMeta bMeta = stack.getItemMeta();

            // Display name
            StringBuilder sb = new StringBuilder();
            if (meta.getName() != null) {
                sb.append(ChatUtils.colorize(meta.getName()));
            } else {
                if (meta.getTier() != null) {
                    sb.append(meta.getTier().getAdjective(type)).append(" ");
                }
                sb.append(type.toString().toLowerCase().replace('_', ' '));
            }
            bMeta.setDisplayName(sb.toString());

            // Data value
            stack.getData().setData(data);

            customItems.put(id, stack);
        }
    }

    @Override
    protected void onStop() {
        customItems.clear();
        metaCache.clear();
    }

    /*
     * Item Meta below this point
     */
    public ItemStack getItem(String id) {
        return customItems.get(id);
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
        }

        // Cache the metadata
        metaCache.put(stack, meta);

        return meta;
    }

    private YamlConfig getConfig(ItemMeta meta) {
        return new YamlConfig(plugin, new File(metaLocation, meta.getUniqueId().toString()), false);
    }

    private void uncacheMeta(ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            metaCache.remove(stack);
        }
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
}
