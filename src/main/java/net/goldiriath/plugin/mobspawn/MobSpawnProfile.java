package net.goldiriath.plugin.mobspawn;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.mobspawn.citizens.HostileMobBehavior;
import net.goldiriath.plugin.mobspawn.citizens.MobSpawnTrait;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobSpawnProfile implements ConfigLoadable, Validatable {

    public static final int INFINITE_POTION_DURATION = 1000000;
    //
    @Getter
    private final String id;
    private final MobSpawnManager msm;
    private final Logger logger;
    //
    @Getter
    private final Set<PotionEffect> effects;
    @Getter
    private EntityType type;
    @Getter
    private int level;
    @Getter
    private String customName;
    @Getter
    private long timeThreshold;
    @Getter
    private ItemStack item;
    @Getter
    private ItemStack helmet;
    @Getter
    private ItemStack chestplate;
    @Getter
    private ItemStack leggings;
    @Getter
    private ItemStack boots;

    public MobSpawnProfile(MobSpawnManager msm, String id) {
        this.id = id;
        this.msm = msm;
        this.logger = msm.getPlugin().getLogger();
        this.effects = new HashSet<>();
    }

    public boolean hasTimeThreshold() {
        return timeThreshold > 0;
    }

    public NPC spawn(Location location) {
        final NPC npc;

        if (type == EntityType.PLAYER) {
            throw new UnsupportedOperationException("Players are not yet supported!");
        } else {
            npc = msm.getBridge().createMob(type);
        }

        // Spawn
        npc.spawn(location);
        npc.setProtected(false);
        npc.getDefaultGoalController().addBehavior(new HostileMobBehavior(msm.getPlugin(), npc, location, 15), 1);

        // Setup
        final Entity entity = npc.getEntity();

        if (!(entity instanceof LivingEntity)) {
            npc.destroy();
            throw new UnsupportedOperationException("Non-living entities are not supported!");
        }

        final LivingEntity mob = (LivingEntity) entity;

        // Set baby
        if (mob instanceof Zombie) {
            ((Zombie) mob).setBaby(false);
        } else if (entity instanceof Ageable) {
            ((Ageable) mob).setAdult();
        }

        mob.addPotionEffects(effects);
        mob.setCanPickupItems(false);
        if (customName != null) {
            npc.setName(customName);
            mob.setCustomName(customName);
            mob.setCustomNameVisible(true);
        }

        // Set equipment
        final EntityEquipment equipment = mob.getEquipment();

        equipment.setItemInHandDropChance(0);
        if (item != null) {
            equipment.setItemInHand(item);
        }

        equipment.setHelmetDropChance(0);
        if (helmet != null) {
            equipment.setHelmet(helmet);
        }

        equipment.setChestplateDropChance(0);
        if (chestplate != null) {
            equipment.setChestplate(chestplate);
        }

        equipment.setLeggingsDropChance(0);
        if (leggings != null) {
            equipment.setLeggings(leggings);
        }

        equipment.setBootsDropChance(0);
        if (boots != null) {
            equipment.setBoots(boots);
        }

        return npc;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {

        final String entityTypeName = config.getString("type", null);
        if (entityTypeName == null) {
            type = null;
        } else {
            type = EntityType.fromName(entityTypeName);
        }

        // Meta
        customName = config.getString("name", null);
        timeThreshold = config.getInt("spawn_threshold", -1);
        level = config.getInt("level", 1);

        // Effects
        effects.clear();
        if (config.isConfigurationSection("potions")) {
            for (String potionTypeName : config.getConfigurationSection("potions").getKeys(false)) {
                final PotionEffectType effectType = PotionEffectType.getByName(potionTypeName);
                if (effectType == null) {
                    logger.warning("Ignoring potion effect for profile '" + id + "'. Unrecognised potion type: " + potionTypeName + "!");
                    continue;
                }

                final int amplifier = config.getInt("potions." + potionTypeName + ".amplifier", 1);
                final boolean ambient = config.getBoolean("potions." + potionTypeName + ".ambient", false);
                int duration = config.getInt("potions." + potionTypeName + ".duration", 200);
                if (duration <= 0) {
                    duration = INFINITE_POTION_DURATION;
                }

                final PotionEffect effect = new PotionEffect(effectType, duration, amplifier, ambient);
                effects.add(effect);
            }
        }

        // Equipment
        item = Util.parseItem(config.getString("item", null));
        helmet = Util.parseItem(config.getString("helmet", null));
        chestplate = Util.parseItem(config.getString("chestplate", null));
        leggings = Util.parseItem(config.getString("leggings", null));
        boots = Util.parseItem(config.getString("boots", null));

        if (customName != null) {
            customName = ChatColor.translateAlternateColorCodes('&', customName);
        }
    }

    @Override
    public boolean isValid() {
        return id != null
                && type != null
                && type.isAlive()
                && type.isSpawnable();
    }

}
