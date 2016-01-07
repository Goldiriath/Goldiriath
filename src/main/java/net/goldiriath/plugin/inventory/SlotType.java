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

    ANY(Material.AIR, 0) {

                @Override
                public boolean validate(ItemStack stack) {
                    return true;
                }

            },
    WEAPON(Material.THIN_GLASS, 0x15, 0) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isWeapon(stack.getType());
                }

            },
    SKILL(Material.THIN_GLASS, 0x14, 1, 2, 3, 4, 5) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isSkill(stack);
                }

            };
    //
    private static final List<ItemStack> placeHolders;
    //
    @Getter
    private final ItemStack placeHolder;

    static {
        List<ItemStack> holders = new ArrayList<>();
        for (SlotType slot : values()) {
            if (slot != ANY) {
                holders.add(slot.placeHolder);
            }
        }
        placeHolders = Collections.unmodifiableList(holders);
    }

    @Getter
    private final int[] indices;

    @SuppressWarnings("deprecation")
    private SlotType(Material material, int dataInt, int... indices) {
        this.placeHolder = new ItemStack(material, 1);
        MaterialData data = placeHolder.getData().clone();
        data.setData(new Integer(dataInt).byteValue());
        placeHolder.setData(data);

        this.indices = indices;
    }

    public boolean hasPlaceHolder() {
        return this != ANY;
    }

    public static boolean isPlaceHolder(ItemStack stack) {
        return !InventoryUtil.isEmpty(stack) && placeHolders.contains(stack);
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
