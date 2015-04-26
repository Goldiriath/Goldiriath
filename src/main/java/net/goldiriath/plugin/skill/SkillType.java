package net.goldiriath.plugin.skill;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum SkillType {

    MAGIC("Magic", Material.POTION, 1, Magic.class),
    ARCHER("Archer", Material.BOW, 1, Archer.class),
    SWORD("Sword", Material.STONE_SWORD, 1, Sword.class),
    ASSASSIN("Assassin", Material.SHEARS, SWORD, 1, Assassin.class);

    private final String name;
    private final Material display;
    private final SkillType reqSkill;
    private final int reqLvl;
    private final Class<? extends Skill> skill;

    private SkillType(String name, Material display, int reqLvl, Class<? extends Skill> clazz) {
        this(name, display, null, reqLvl, clazz);
    }

    private SkillType(String name, Material display, SkillType reqSkill, int reqLvl, Class<? extends Skill> clazz) {
        this.name = name;
        this.display = display;
        this.reqSkill = reqSkill;
        this.reqLvl = reqLvl;
        this.skill = clazz;
    }

    public String getName() {
        return name;
    }

    public Material getDisplay() {
        return display;
    }

    public SkillType getReqSkill() {
        return reqSkill;
    }

    public int getReqlvl() {
        return reqLvl;
    }

    public Class<? extends Skill> getSkillClass() {
        return skill;
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

    public static SkillType fromName(String name) {
        for (SkillType type : SkillType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null; //TODO FIX
    }

}
