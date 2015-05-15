package me.dirkjan.goldiriath.mobspawn;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class Profile {

    private final String name;
    private final String customName;
    private final ItemStack carryItem;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;

    public Profile(String name, String customName, ItemStack carryItem, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        this.name = name;
        this.customName = customName;
        this.carryItem = carryItem;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public String getName() {
        return name;
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

    public void setup(LivingEntity entity) {
        final EntityEquipment equipment = entity.getEquipment();

        entity.setCanPickupItems(false);
        equipment.setItemInHandDropChance(0);
        equipment.setHelmetDropChance(0);
        equipment.setChestplateDropChance(0);
        equipment.setLeggingsDropChance(0);
        equipment.setBootsDropChance(0);

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
    }

}
