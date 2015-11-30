package net.goldiriath.plugin.item;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Maps;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemManager extends AbstractService {

    @Getter
    private final Map<String, ItemStack> itemMap = Maps.newHashMap();
    private final YamlConfig config;

    public CustomItemManager(Goldiriath plugin) {
        super(plugin);
        this.config = new YamlConfig(plugin, "items.yml");
    }

    @Override
    protected void onStart() {

        // Load config
        config.load();

        // Load custom items
        itemMap.clear();
        for (String id : config.getKeys(false)) {
            // ID
            if (!config.isConfigurationSection(id)) {
                logger.warning("Skipping item: " + id + ". Incorrect format!");
                continue;
            }
            ConfigurationSection section = config.getConfigurationSection(id);

            if (!id.toLowerCase().equals(id)) {
                logger.warning("Converting nonstandard item id to lower case: " + id);
            }
            id = id.toLowerCase();

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

            // No validation below this point
            // Create itemstack
            final ItemStack stack = new ItemStack(type, 1);

            // Create and load metadata, if present
            UUID stackUuid = UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8));
            final GItemMeta meta = GItemMeta.createItemMeta(stack, stackUuid);
            plugin.im.getItemMeta().getMetaCache().put(stackUuid, meta);
            if (section.isConfigurationSection("meta")) {
                meta.loadFrom(section.getConfigurationSection("meta"));
            }

            // Set bukkit GItemMeta properties below this point
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

            itemMap.put(id, stack);
        }
    }

    @Override
    protected void onStop() {
        itemMap.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent event) {
        // Update inventory when switching gamemodes
        // This is done so the inventory's itemmeta is re-sent
        event.getPlayer().updateInventory();
    }

}
