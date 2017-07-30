package net.goldiriath.plugin.game.inventory;

import lombok.Getter;
import net.goldiriath.plugin.game.item.StaticItem;
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
    SKILL_BOOK(5) {

        @Override
        public boolean validate(ItemStack stack) {
            return InventoryUtil.isSkillBook(stack);
        }

    },
    SKILL(1, 2, 3, 4) {

        @Override
        public boolean validate(ItemStack stack
        ) {
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

        // Item slots overview:
        // https://bugs.mojang.com/secure/attachment/61101/Items_slot_number.jpg
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
