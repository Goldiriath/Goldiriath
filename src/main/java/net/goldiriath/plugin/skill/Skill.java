package net.goldiriath.plugin.skill;

import org.bukkit.entity.Player;

public abstract class Skill {

    protected final SkillType type;
    protected final Player player;
    protected final int lvl;

    protected Skill(SkillType type, Player player, int lvl) {
        this.type = type;
        this.player = player;
        this.lvl = lvl;

    }

    public SkillType getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLvl() {
        return lvl;
    }

    public abstract void use(); // Should be called when the skill is used

}
