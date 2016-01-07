package net.goldiriath.plugin.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.goldiriath.plugin.util.Util;
import net.pravian.bukkitlib.util.MaterialUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public enum SlotType implements ItemStackValidatable {

    ANY(Material.AIR, (byte) 0) {

                @Override
                public boolean validate(ItemStack stack) {
                    return true;
                }

            },
    WEAPON(Material.STAINED_GLASS_PANE, 14, 0) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isWeapon(stack.getType());
                }

            },
    SKILL(Material.STAINED_GLASS_PANE, 15, 1, 2, 3, 4, 5) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isSkill(stack);
                }

            };
    //
    @Getter
    private final ItemStack placeHolder;

    @Getter
    private final int[] indices;

    @SuppressWarnings("deprecation")
    private SlotType(Material material, int durability, int... indices) {
        this.placeHolder = new ItemStack(material, 1);
        this.placeHolder.setDurability((short) durability);
        this.indices = indices;
    }

    public boolean hasPlaceHolder() {
        return this != ANY;
    }

    public static boolean isPlaceHolder(ItemStack stack) {
        if (InventoryUtil.isEmpty(stack)) {
            return false;
        }

        for (SlotType type : SlotType.values()) {
            if (type.hasPlaceHolder() && type.getPlaceHolder().equals(stack)) {
                return true;
            }
        }

        return false;
    }

    public static SlotType ofIndex(int targetIndex) {

        for (SlotType slot : values()) {
            if (slot == ANY) {
                continue;
            }

            for (int loopIndex : slot.indices) {
                if (loopIndex == targetIndex) {
                    return slot;
                }
            }
        }

        return ANY;
    }

}
