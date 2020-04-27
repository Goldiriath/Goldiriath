package net.goldiriath.plugin.game.item;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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

            // Enchantments
            final Map<Enchantment, Integer> enchantments = new HashMap<>();
            ConfigurationSection enchantmentsSection = section.getConfigurationSection("enchantments");
            if (enchantmentsSection != null) {
                for (String name : enchantmentsSection.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByName(name.toUpperCase());
                    if (enchantment == null) {
                        logger.warning("Ignoring unknown enchantment: " + name.toUpperCase() + " for custom item " + id);
                        continue;
                    }

                    int level = enchantmentsSection.getInt(name, 1);
                    enchantments.put(enchantment, level);
                }
            }

            // Create UUID and itemstack
            ItemStack stack = new ItemStack(type, 1);

            // Set data loaded from this config here.
            stack.addEnchantments(enchantments);
            stack.getData().setData(data);

            // Create and load metadata, if present
            UUID stackUuid = UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8));
            final GItemMeta meta = GItemMeta.createItemMeta(stack, stackUuid);
            plugin.im.getItemMeta().getMetaCache().put(stackUuid, meta);

            // Load metadata from this section
            // Most importantly: name, level, (armor type), tier, and lore
            meta.loadFrom(section);

            // Validate armor type
            if (meta.getArmorType() != null && !InventoryUtil.isArmor(type)) {
                logger.warning("Skipping item: " + id + ". Armor type specified for non-armor!");
                continue;
            }
            if (meta.getArmorType() == null && InventoryUtil.isArmor(type)) {
                logger.warning("Skipping item: " + id + ". No armor type specified for armor!");
                continue;
            }

            itemMap.put(id, stack);
        }

        logger.info("Loaded " + itemMap.size() + " custom items");
    }

    @Override
    protected void onStop() {
        itemMap.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent event) {
        // Update inventory when switching gamemodes.
        // This is done so the inventory's itemmeta is re-sent.
        // This is needed to refresh the associated lore with items,
        // since we are modifying packets before they're sent.
        event.getPlayer().updateInventory();
    }

}
