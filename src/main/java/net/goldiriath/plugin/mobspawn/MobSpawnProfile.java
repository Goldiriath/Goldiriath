package net.goldiriath.plugin.mobspawn;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawnProfile implements ConfigLoadable, Validatable {

    public static final int INFINITE_POTION_DURATION = 1000000;
    //
    private final String id;
    private final MobSpawnManager msm;
    private final Logger logger;
    //
    private final Set<PotionEffect> effects;
    private EntityType type;
    private int level;
    private String customName;
    private long spawnThreshold;
    private ItemStack carryItem;
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    public MobSpawnProfile(MobSpawnManager msm, String id) {
        this.id = id;
        this.msm = msm;
        this.logger = msm.getPlugin().getLogger();
        this.effects = new HashSet<>();
    }

    public MobSpawnManager getManager() {
        return msm;
    }

    public String getId() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public boolean hasSpawnThreshold() {
        return spawnThreshold >= 0;
    }

    public long getSpawnThreshold() {
        return spawnThreshold;
    }

    public String getCustomName() {
        return customName;
    }

    public ItemStack getCarryItem() {
        return carryItem;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
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
        spawnThreshold = config.getInt("spawn_threshold", -1);
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
        carryItem = Util.parseItem(config.getString("item", null));
        helmet = Util.parseItem(config.getString("helmet", null));
        chestplate = Util.parseItem(config.getString("chestplate", null));
        leggings = Util.parseItem(config.getString("leggings", null));
        boots = Util.parseItem(config.getString("boots", null));

        if (customName != null) {
            customName = ChatColor.translateAlternateColorCodes('&', customName);
        }
    }

    public LivingEntity spawn(Location location) {
        if (!isValid()) {
            return null;
        }

        // Spawn entity
        final LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);

        // Set baby
        if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby(false);
        } else if (entity instanceof Ageable) {
            ((Ageable) entity).setAdult();
        }

        // Add potion effects
        entity.addPotionEffects(effects);

        // TODO: Find a better way to keep infinite potion effects lasting?
        for (PotionEffect effect : effects) {
            if (effect.getDuration() != INFINITE_POTION_DURATION) {
                continue;
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!entity.isValid() || entity.isDead()) {
                        cancel();
                        return;
                    }

                    // Update potion effects
                    for (PotionEffect effect : effects) {
                        entity.addPotionEffect(effect, true);
                    }
                }

            }.runTaskLater(msm.getPlugin(), 20);
            break;
        }

        // Set equipment
        final EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInHandDropChance(0);
        equipment.setHelmetDropChance(0);
        equipment.setChestplateDropChance(0);
        equipment.setLeggingsDropChance(0);
        equipment.setBootsDropChance(0);
        entity.setCanPickupItems(false);

        if (customName != null) {
            entity.setCustomName(customName);
        }

        if (carryItem != null) {
            equipment.setItemInHand(carryItem);
        }

        if (helmet != null) {
            equipment.setHelmet(helmet);
        }

        if (chestplate != null) {
            equipment.setChestplate(chestplate);
        }

        if (leggings != null) {
            equipment.setLeggings(leggings);
        }

        if (boots != null) {
            equipment.setBoots(boots);
        }

        return entity;
    }

    @Override
    public boolean isValid() {
        return id != null
                && type != null
                && type.isAlive()
                && type.isSpawnable();
    }

}
