
package net.goldiriath.plugin.player.data;

import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.logging.GLogger;
import net.goldiriath.plugin.player.AbstractAttachement;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import org.bukkit.configuration.ConfigurationSection;

public abstract class Data extends AbstractAttachement implements ConfigLoadable, ConfigSavable {

    @Getter
    private final String key;;

    public Data(PlayerData data, String key) {
        super(data);
        this.key = key;
    }

    protected abstract void load(ConfigurationSection config);

    protected abstract void save(ConfigurationSection config);

    @Override
    public final void loadFrom(ConfigurationSection config) {
        if (!config.isConfigurationSection(key)) {
            return;
        }

        load(config.getConfigurationSection(key));
    }

    @Override
    public final void saveTo(ConfigurationSection config) {
        save(config.createSection(key));
    }



}
