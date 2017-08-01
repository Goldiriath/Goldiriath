package net.goldiriath.plugin.game.skill;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.type.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum SkillType {

    //sword skills:
    SWORD_PROTECTIVE_FORMATION(
            SwordProtectiveFormation.class,
            "Protective Formation",
            WeaponType.SWORD,
            50,
            60 * 20,
            1,
            StaticItem.SKILL_SWORD_PROTECTIVE_FORMATION
    ),
    SWORD_HOLY_SLAM(
            SwordHolySlam.class,
            "Holy Slam",
            WeaponType.SWORD,
            50,
            10 * 20,
            0,
            StaticItem.SKILL_SWORD_HOLY_SLAM
    ),
    SWORD_BLESSING(
            SwordBlessing.class,
            "Blessing",
            WeaponType.SWORD,
            10,
            20 * 20,
            0,
            StaticItem.SKILL_SWORD_BLESSING
    ),
    SWORD_DEVINE_LIGHT(
            SwordDevineLight.class,
            "Devine Light",
            WeaponType.SWORD,
            20,
            25 * 20,
            1,
            StaticItem.SKILL_SWORD_DIVINE_LIGHT
    ),

    //bow skills:
    BOW_SPREAD_SHOT(
            BowSpreadShot.class,
            "Spread shot",
            WeaponType.BOW,
            25,
            20 * 20,
            1,
            StaticItem.SKILL_BOW_SPREADSHOT
    ),
    BOW_PUNCH_SHOT(
            BowPunchShot.class,
            "Punch shot",
            WeaponType.BOW,
            20,
            25 * 20,
            2,
            StaticItem.SKILL_BOW_PUNCHSHOT
    ),
    BOW_BLEEDING_ARROWS(
            BowBleedingArrows.class,
            "Bleeding Arrows",
            WeaponType.BOW,
            30,
            20 * 20,
            3,
            StaticItem.SKILL_BOW_BLEEDING_ARROWS
    ),
    BOW_POWERSHOT(
            BowPowerShot.class,
            "Powershot",
            WeaponType.BOW,
            50,
            15 * 20,
            0,
            StaticItem.SKILL_BOW_POWERSHOT
    );

    //
    @Getter
    private final Class<? extends Skill> skillClass;
    @Getter
    private final String name;
    @Getter
    private final WeaponType weapon;
    @Getter
    private final int manaCost;
    @Getter
    private final int delayTicks;
    @Getter
    private final int reqSkills;
    @Getter
    private final StaticItem display;

    // Passive skill constructor
    private SkillType(Class<? extends Skill> clazz, String name, WeaponType type, int required, StaticItem display) {
        this(clazz, name, type, 0, 0, required, display);
    }

    // Active skill constructor
    private SkillType(Class<? extends Skill> clazz, String name, WeaponType type, int manaCost, int cooldown, int required, StaticItem display) {
        this.skillClass = clazz;
        this.name = name;
        this.weapon = type;
        this.manaCost = manaCost;
        this.delayTicks = cooldown;
        this.reqSkills = required;
        this.display = display;
    }

    public Skill create(Player player, SkillMeta meta) {
        if (meta.type != this) {
            throw new IllegalArgumentException("Could not create skill from skilltype. Invalid meta type!");
        }

        try {
            Constructor<? extends Skill> constructor = skillClass.getConstructor(
                    SkillMeta.class,
                    Player.class
            );

            return constructor.newInstance(meta, player);
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
        return null;
    }

    public static SkillType fromDisplay(ItemStack stack) {
        for (SkillType type : SkillType.values()) {
            if (type.getDisplay().getStack().equals(stack)) {
                return type;
            }
        }
        return null;
    }

    public static List<SkillType> findForWeapon(WeaponType weapon) {
        List<SkillType> skills = new ArrayList<>();

        for (SkillType t : SkillType.values()) {
            if (t.getWeapon() == weapon) {
                skills.add(t);
            }
        }

        return skills;
    }

}
