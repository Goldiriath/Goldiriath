package me.dirkjan.goldiriath.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import net.pravian.bukkitlib.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemStorage extends AbstractService {

    private final YamlConfig config;
    private final Map<String, ItemStack> items;

    public ItemStorage(Goldiriath plugin) {
        super(plugin);
        this.config = new YamlConfig(plugin, "items.yml");
        this.items = new HashMap<>();
    }

    @Override
    protected void onStart() {
        items.clear();
        config.load();

        for (String id : config.getKeys(false)) {

            // ID
            if (!config.isConfigurationSection(id)) {
                logger.warning("Skipping item: " + id + ". Incorrect format!");
                continue;
            }
            ConfigurationSection section = config.getConfigurationSection(id);

            // Name
            String name = section.getString("name", null);
            if (name == null) {
                logger.warning("Skipping item: " + id + ". Missing name!");
                continue;
            }
            name = ChatUtils.colorize(name);

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

            // Level
            int level = section.getInt("level", 1);
            if (level < 1) {
                logger.warning("Skipping item: " + id + ". Invalid level: " + data + "!");
                continue;
            }

            // Tier
            String tierString = section.getString("tier", null);
            if (tierString == null) {
                tierString = ItemTier.BATTERED.getAdjective();
            }
            ItemTier tier = ItemTier.fromName(tierString);
            if (tier == null) {
                logger.warning("Skipping item: " + id + ". Could not determine tier!");
                continue;
            }

            // Lore
            List<String> lore = new ArrayList<>();
            for (String loreString : section.getStringList("lore")) {
                lore.add(ChatUtils.colorize(loreString));
            }

            ItemStack stack = ItemFactory
                    .builder(type)
                    //.withName(name) // TODO implement
                    .withData(data)
                    .withLevel(level)
                    .withTier(tier)
                    .withLore(lore.toArray(new String[lore.size()]))
                    .build();

            items.put(id, stack);
        }
    }

    @Override
    protected void onStop() {
    }

    public ItemStack getItem(String id) {
        return items.get(id);
    }

}
