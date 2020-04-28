package net.goldiriath.plugin.game.loot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class LootProfile implements ConfigLoadable {

    private final Goldiriath plugin;
    @Getter
    private final String id;
    @Getter
    private final Map<Integer, Table> tableMap = Maps.newHashMap();

    public LootProfile(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        tableMap.clear();

        //Loads Tables
        for (String keyString : config.getKeys(false)) {

            // Checks if key is valid
            int key;
            try {
                key = Integer.parseInt(keyString);
            } catch (Exception e) {
                plugin.logger.warning("Could not parse table tier: " + keyString);
                return;
            }

            Table table = new Table(key);

            // Parses drops into a table
            for (String drop : config.getStringList(keyString + ".drops")) {
                String[] dropArray = drop.split(" ");
                int amt;
                int weight;

                try {
                    amt = Integer.parseInt(dropArray[0]);
                    weight = Integer.parseInt(dropArray[1]);
                } catch (Exception e) {
                    plugin.logger.warning("Could not parse drop: " + drop);
                    return;
                }
                table.addDrop(amt, weight);
            }

            // Parses items into a table
            for (String item : config.getStringList(keyString + ".items")) {

                String[] itemArray = item.split(" ");

                // Checks if arraylengt is 3
                if (itemArray.length != 3) {
                    plugin.logger.warning("Item improperly defined: " + item);
                    return;
                }

                // Parses weight
                int weight;
                try {
                    weight = Integer.parseInt(itemArray[2]);
                } catch (Exception e) {
                    plugin.logger.warning("Could not parse weight: " + itemArray[2]);
                    return;
                }

                // Determines itemcontainer
                ItemContainer cunt = null;
                if (itemArray[0].equals("group")) {
                    Group group = plugin.ltm.getGroupMap().get(itemArray[1]);
                    if (group == null) {
                        plugin.logger.warning("error 404 group: " + itemArray[1] + " not found");
                        return;
                    }
                    cunt = new ItemContainer(group);
                } else {
                    int amt;
                    try {
                        amt = Integer.parseInt(itemArray[0]);
                    } catch (Exception e) {
                        plugin.logger.warning("Could not parse amount: " + itemArray[0]);
                        return;
                    }
                    ItemStack stack = Util.parseItem(itemArray[1]);
                    if (stack == null) {
                        plugin.logger.warning("Could not parse item: " + itemArray[1]);
                        return;
                    }
                    stack.setAmount(amt);
                    cunt = new ItemContainer(stack);
                }

                table.addItem(cunt, weight);
            }
            tableMap.put(key, table);
        }
    }

    public List<ItemStack> drop(TierContainer tier) {
        List<ItemStack> drops = Lists.newArrayList();

        for (Table table : tableMap.values()) {
            if (table.getTier() > tier.getTierValue()) {
                continue;
            }

            drops.addAll(table.drop());
        }

        return drops;
    }

}
