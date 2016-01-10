package net.goldiriath.plugin.inventory;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public enum SlotType implements ItemStackValidatable {

    ANY() {

                @Override
                public boolean validate(ItemStack stack) {
                    return true;
                }

            },
    WEAPON(0) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isWeapon(stack.getType());
                }

            },
    SKILL(1, 2, 3, 4, 5) {

                @Override
                public boolean validate(ItemStack stack) {
                    return InventoryUtil.isSkill(stack);
                }

            };
    //
    @Getter
    private final int[] indices;

    @SuppressWarnings("deprecation")
    private SlotType(int... indices) {
        this.indices = indices;
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
