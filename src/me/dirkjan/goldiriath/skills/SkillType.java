package me.dirkjan.goldiriath.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

public enum SkillType {

    ARCHER("archer", 1, Archer.class),
    SWORD("sword", 1, Sword.class),
    MAGIC("magic", 1, Magic.class),
    ASSASSIN("assassin", 1, Assassin.class);

    private final Class<? extends Skill> skill;
    private final String name;
    private final int reqlvl;

    private SkillType(String name, int reqlvl, Class<? extends Skill> clazz) {
        this.skill = clazz;
        this.name = name;
        this.reqlvl = reqlvl;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Skill> getSkillClass() {
        return skill;
    }

    public int getReqlvl() {
        return reqlvl;
    }

    public Skill create(Player player, int lvl) {
        try {
            Constructor<? extends Skill> constructor = skill.getConstructor(SkillType.class, Player.class, int.class);
            return constructor.newInstance(this, player, lvl);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            Logger.getLogger(SkillType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
