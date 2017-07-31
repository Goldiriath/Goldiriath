package net.goldiriath.plugin.game.skill.type;

import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.entity.Player;

public abstract class Skill {

    protected final Goldiriath plugin;
    @Getter
    protected final SkillType type;
    @Getter
    protected final SkillMeta meta;
    @Getter
    protected final Player player;
    @Setter
    @Getter
    protected long lastUse;

    public Skill(SkillMeta meta, Player player) {
        this.plugin = Goldiriath.instance();
        this.type = meta.type;
        this.meta = meta;
        this.player = player;
    }

    protected PlayerData data() {
        return plugin.pm.getData(player);
    }

    protected PlayerData data(Player player) {
        return plugin.pm.getData(player);
    }
}
