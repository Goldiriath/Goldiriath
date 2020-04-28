package net.goldiriath.plugin.game.mobspawn;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.loot.LootProfile;
import net.goldiriath.plugin.game.citizens.HostileMobBehavior;
import net.goldiriath.plugin.game.citizens.HostileMobTrait;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import net.pravian.aero.component.PluginComponent;
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

public class MobSpawnProfile extends PluginComponent<Goldiriath> implements ConfigLoadable, Validatable {

    public static final int INFINITE_POTION_DURATION = 1000000;
    public static final int WANDER_RANGE = 15;
    //
    @Getter
    private final String id;
    @Getter
    private final MobSpawnManager manager;
    private final Logger logger;
    //
    @Getter
    private String customName;
    @Getter
    private long timeThreshold;
    @Getter
    private EntityType type;
    @Getter
    private int level;
    @Getter
    private int damage;
    @Getter
    private int health;
    @Getter
    private MobTier lootTier;
    @Getter
    private LootProfile lootProfile;
    @Getter
    private ItemStack hand;
    @Getter
    private ItemStack helmet;
    @Getter
    private ItemStack chestplate;
    @Getter
    private ItemStack leggings;
    @Getter
    private ItemStack boots;
    @Getter
    private final Set<PotionEffect> effects;

    public MobSpawnProfile(MobSpawnManager manager, String id) {
        super(manager.getPlugin());
        this.id = id;
        this.manager = manager;
        this.logger = manager.getPlugin().getLogger();
        this.effects = new HashSet<>();
    }

    public NPC spawn(Location location) {
        final NPC npc;

        if (type == EntityType.PLAYER) {
            throw new UnsupportedOperationException("Players are not yet supported!");
        } else {
            npc = manager.getPlugin().czb.createMob(type);
        }

        // Spawn
        npc.spawn(location);
        npc.setProtected(true);
        npc.addTrait(new HostileMobTrait(this));
        npc.getDefaultGoalController().addBehavior(new HostileMobBehavior(manager.getPlugin(), npc, location, WANDER_RANGE), 1);

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

        // Potion effects
        mob.addPotionEffects(effects);

        // Custom name
        if (customName != null) {
            npc.setName(customName);
            mob.setCustomName(customName);
            mob.setCustomNameVisible(true);
        }

        // Set equipment
        final EntityEquipment equipment = mob.getEquipment();
        mob.setCanPickupItems(false);

        equipment.setItemInHandDropChance(0);
        if (hand != null) {
            equipment.setItemInHand(hand);
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

        // Meta
        customName = config.getString("name", null);
        if (customName != null) {
            customName = ChatColor.translateAlternateColorCodes('&', customName);
        }
        timeThreshold = config.getInt("spawn_threshold", -1);

        final String entityTypeName = config.getString("type", null);
        if (entityTypeName == null) {
            type = null;
        } else {
            try {
                type = EntityType.valueOf(entityTypeName);
            } catch (IllegalArgumentException ex) {
                type = null;
                logger.warning("Could not load mobspawn profile '" + id + "'. Invalid entity type: " + entityTypeName);
            }
        }

        // Stats
        level = config.getInt("stats.level", -1);
        if (level == -1) {
            logger.warning("Could not load mobspawn profile '" + id + "'. Level field not present.");
        }
        damage = config.getInt("stats.damage", -1);
        health = config.getInt("stats.health", -1);
        if (health == -1) {
            logger.warning("Could not load mobspawn profile '" + id + "'. Health field not present.");
        }

        // Loot
        if (!config.contains("loot")) {
            lootProfile = null;
            lootTier = null;
        } else {
            String profileId = config.getString("loot.profile", null);
            lootProfile = plugin.ltm.getProfile(profileId);
            if (lootProfile == null) {
                logger.warning("Could not load mobspawn profile '" + id + "'. Invalid loot table id: " + profileId);
            }
            String tierName = config.getString("loot.tier", null);
            try {
                // TODO: Numerical mob tier support
                lootTier = MobTier.valueOf(tierName);
            } catch (Exception ex) {
                lootTier = null;
                logger.warning("Could not load mobspawn profile '" + id + "'. Invalid loot table id: " + tierName);

            }
        }

        // Items
        if (!config.contains("items")) {
            hand = null;
            helmet = null;
            chestplate = null;
            leggings = null;
            boots = null;
        } else {
            String s = config.getString("items.hand", null);
            if (s != null) {
                hand = Util.parseItem(s);
                if (hand == null) {
                    logger.warning("Could not load mobspawn profile '" + id + "'. Invalid item: " + s);
                }
            }
            s = config.getString("items.helmet", null);
            if (s != null) {
                helmet = Util.parseItem(s);
                if (helmet == null) {
                    logger.warning("Could not load mobspawn profile '" + id + "'. Invalid item: " + s);
                }
            }
            s = config.getString("items.chestplate", null);
            if (s != null) {
                chestplate = Util.parseItem(s);
                if (chestplate == null) {
                    logger.warning("Could not load mobspawn profile '" + id + "'. Invalid item: " + s);
                }
            }
            s = config.getString("items.leggings", null);
            if (s != null) {
                leggings = Util.parseItem(s);
                if (leggings == null) {
                    logger.warning("Could not load mobspawn profile '" + id + "'. Invalid item: " + s);
                }
            }
            s = config.getString("items.boots", null);
            if (s != null) {
                boots = Util.parseItem(s);
                if (boots == null) {
                    logger.warning("Could not load mobspawn profile '" + id + "'. Invalid item: " + s);
                }
            }
        }

        if (damage == -1 && hand == null) {
            logger.warning("Could not load mobspawn profile '" + id + "'. Damage field not present and item not present. Must specify either to calculate attack damage.");
        }

        // Effects
        effects.clear();
        if (config.contains("potions")) {
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
    }

    public boolean hasTimeThreshold() {
        return timeThreshold >= 0;
    }

    @Override
    public boolean isValid() {
        return id != null
                && type != null
                && type.isAlive()
                && type.isSpawnable()
                && level != -1
                && (damage != -1 || hand != null)
                && health != -1;
    }

}
