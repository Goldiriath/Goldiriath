package net.goldiriath.plugin.skill;

import java.util.Objects;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Skill)) {
            return false;
        }

        // TODO: Better
        return hashCode() == ((Skill) obj).hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }

}
