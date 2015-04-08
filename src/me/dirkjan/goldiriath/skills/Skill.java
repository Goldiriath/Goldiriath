package me.dirkjan.goldiriath.skills;

import org.bukkit.entity.Player;

public abstract class Skill {

    private final SkillType type;
    private final Player player;
    private final int lvl;
    
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

}
