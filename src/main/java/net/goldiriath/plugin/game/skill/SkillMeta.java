package net.goldiriath.plugin.game.skill;

import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.ConfigSavable;
import org.bukkit.configuration.ConfigurationSection;

public class SkillMeta implements ConfigLoadable, ConfigSavable {

    public final SkillType type;
    //
    public int level;

    //
    public SkillMeta(SkillType type) {
        this.type = type;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        this.level = config.getInt("level", 0);
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        config.set("level", level);
    }

}
