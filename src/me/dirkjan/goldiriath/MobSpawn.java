package me.dirkjan.goldiriath;

import static me.dirkjan.goldiriath.Goldriath.plugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawn {

    private EntityType type;
    private Location location;
    private int lvl;
    private String name;

    public EntityType getEntityType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public int getLvl() {
        return lvl;
    }

    public String getName() {
        return name;
    }

    public void setEntityType(EntityType type) {
        this.type = type;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return type != null && location != null && lvl != 0 && name != null;
    }

    private void tick() {
        if (!isValid()) {
            plugin.getLogger().severe("not valid");
            return;
        }
        int closemobs = 0;
        for (Entity entity : location.getWorld().getLivingEntities()) {
            if (entity.getLocation().distanceSquared(location) < 625) {
                closemobs++;
            }
        }
        if (closemobs < 6) {
            spawn();
        }
    }

    public void spawn() {
        if (!isValid()) {
            return;
        }

        Entity mob = location.getWorld().spawnEntity(location, type);
        LivingEntity le = (LivingEntity) mob;
        le.setCustomName(name);
        if (le.getCustomName().equals("zombie(lvl5)")) {
            if (lvl == 5) {
                le.setCanPickupItems(false);
                le.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                le.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                le.getEquipment().setLeggings(null);
                le.getEquipment().setHelmet(null);
                le.getEquipment().setItemInHand(Items.WOODEN_SWORD.getItem());
                le.getEquipment().setBootsDropChance(0);
                le.getEquipment().setChestplateDropChance(0);
                le.getEquipment().setHelmetDropChance(0);
                le.getEquipment().setItemInHandDropChance(1);
                le.getEquipment().setLeggingsDropChance(0);

            }
        }
    }

    public void startspawning() {
        new BukkitRunnable() {

            @Override
            public void run() {

                tick();

            }
        }.runTaskTimer(Goldriath.plugin, 20, 20);
    }
}
