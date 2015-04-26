package net.goldiriath.plugin.game.loot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import org.bukkit.inventory.ItemStack;

public class Table {

    private static final Random RAND = new Random();

    @Getter
    private final int tier;
    @Getter
    private final Map<Integer, Integer> dropMap = Maps.newHashMap();// drops , weight
    private int totalDropWeight = 0;
    @Getter
    private final Map<ItemContainer, Integer> itemMap = Maps.newHashMap(); //item, weight
    private int totalItemWeight;

    public Table(int tier) {
        this.tier = tier;

    }

    public void addDrop(int drops, int weight) {
        dropMap.put(drops, weight);
        totalDropWeight += weight;
    }

    public void addItem(ItemContainer item, int weight) {
        itemMap.put(item, weight);
        totalItemWeight += weight;
    }

    public void clearDrops() {
        dropMap.clear();
        totalDropWeight = 0;
    }

    public void clearItems() {
        itemMap.clear();
        totalItemWeight = 0;
    }

    public List<ItemStack> drop() {
        List<ItemStack> stacks = Lists.newArrayList();

        // Find the right drop amount
        int dropCount = pickFromWeightMap(dropMap, totalDropWeight);

        if (dropCount < 1) {
            return stacks;
        }

        // Add items
        for (int i = 0; i < dropCount; i++) {
            ItemContainer item = pickFromWeightMap(itemMap, totalItemWeight);
            if (item == null) {
                Goldiriath.instance().logger.warning("Could not pick item from item map!");
                continue;
            }

            stacks.add(item.getItem());
        }

        return stacks;
    }

    private <T> T pickFromWeightMap(Map<T, Integer> weightMap, int totalWeight) {
        int random = RAND.nextInt(totalWeight);

        // Find the right drop amount
        int dropWeight = 0;
        Iterator<Entry<T, Integer>> dropIt = weightMap.entrySet().iterator();
        while (dropIt.hasNext()) {
            Entry<T, Integer> dropnext = dropIt.next();
            dropWeight += dropnext.getValue();

            if (random <= dropWeight) {
                return dropnext.getKey();
            }
        }

        return null;
    }

}
