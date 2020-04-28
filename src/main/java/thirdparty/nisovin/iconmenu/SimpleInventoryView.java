package thirdparty.nisovin.iconmenu;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SimpleInventoryView extends InventoryView {

    private final String title;
    private final HumanEntity player;
    private final Inventory top;
    private final Inventory bottom;

    public SimpleInventoryView(String title, HumanEntity player, Inventory top, Inventory bottom) {
        this.title = title;
        this.player = player;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getTopInventory() {
        return top;
    }

    @Override
    public Inventory getBottomInventory() {
        return bottom;
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public InventoryType getType() {
        InventoryType type = top.getType();
        if (type == InventoryType.CRAFTING && player.getGameMode() == GameMode.CREATIVE) {
            return InventoryType.CREATIVE;
        }
        return type;
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        checkSlot(slot);
        super.setItem(slot, item);
    }

    @Override
    public ItemStack getItem(int slot) {
        checkSlot(slot);
        return super.getItem(slot);
    }

    private void checkSlot(int slot) {
        if (slot == OUTSIDE) {
            return;
        }

        int size = countSlots() + 4; // armor slots
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("Slot out of range [0," + size + "): " + slot);
        }
    }

}
