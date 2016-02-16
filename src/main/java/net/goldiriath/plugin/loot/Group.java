package net.goldiriath.plugin.loot;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Group implements ConfigLoadable {

    @Getter
    private final String id;
    @Getter
    private final List<ItemStack> items = Lists.newArrayList();
    private final Random rn = new Random();

    public Group(String id) {
        this.id = id;
    }

    public void setItems(Collection<ItemStack> toAdd) {
        items.clear();
        items.addAll(toAdd);
    }

    public ItemStack getRandomItem() {
        return items.get(rn.nextInt(items.size()));

    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        for (String raw : config.getStringList(id)) {
            String[] parts = raw.split(" ");
            int amt = parts.length == 2 ? Integer.valueOf(parts[0]) : 1;
            ItemStack item = Util.parseItem(parts.length == 2 ? parts[1] : parts[0]);
            if (item == null) {
                Goldiriath.instance().logger.warning("Could not load group: " + id + ". Could not parse item: " + raw + "!");
                return;
            }
            item.setAmount(amt);
            items.add(item);

        }
    }

}
