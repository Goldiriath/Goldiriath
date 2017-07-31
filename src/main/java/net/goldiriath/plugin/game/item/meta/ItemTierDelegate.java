package net.goldiriath.plugin.game.item.meta;

import java.lang.reflect.Field;
import net.goldiriath.plugin.util.persist.delegate.ConfigDelegate;
import org.bukkit.configuration.ConfigurationSection;

public class ItemTierDelegate extends ConfigDelegate<ItemTier> {

    public ItemTierDelegate(String key) {
        super(key);
    }

    @Override
    public ItemTier loadValue(ConfigurationSection config, Field field) {
        String tierString = config.getString(key, null);
        return tierString == null ? null : ItemTier.fromName(tierString);
    }

    @Override
    public void saveValue(ConfigurationSection config, Object object) {
        config.set(key, ((ItemTier) object).name());
    }

}
