package net.goldiriath.plugin.game.skill.menu;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.SlotType;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.game.skill.type.WeaponType;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WeaponSkillMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    public WeaponSkillMenu(Goldiriath plugin) {
        super(plugin);
    }

    public void openMenu(final Player player, WeaponType weapon) {
        final IconMenu menu = new IconMenu(weapon.name() + " Skills", 27, this, plugin);

        menu.setOption(26, StaticItem.MENU_DONE.getStack(), "done");

        int i = 0;
        for (SkillType s : SkillType.findForWeapon(weapon)) {

            menu.setOption(i, s.getDisplay().getStack(), s.getName());
            i++;
        }

        menu.open(player);
    }

    @Override
    public void onOptionClick(final IconMenu.OptionClickEvent event) {

        if (event.getName().equals("done")) {
            event.setWillClose(true);
            event.setWillDestroy(true);
            return;
        }

        final InventoryClickEvent iClick = event.getEvent();

        // Whether the player clicked the top inventory
        boolean topInventory = iClick.getRawSlot() == iClick.getSlot();

        // Whether the player clicked a skill slot in the top inventory
        boolean clickSkillBook = topInventory && !event.getName().isEmpty();

        // Whether the player clicked a skill slot in the bottom inventory hot bar
        boolean clickSkillSlot = false;

        if (!topInventory) {
            for (int skillIndex : SlotType.SKILL.getIndices()) {
                if (skillIndex == iClick.getSlot()) {
                    clickSkillSlot = true;
                }
            }
        }

        switch (iClick.getAction()) {
            case PICKUP_ONE:
            case PICKUP_ALL: {
                // Allow picking up skills from the hotbar
                if (clickSkillSlot) {
                    iClick.setCancelled(false);
                    break;
                }

                // Allow picking up skills
                if (clickSkillBook) {
                    iClick.setCancelled(false);
                    resetInventoryLater(iClick.getView().getTopInventory(), event.getMenu().getContents());
                    break;
                }
            }

            case PLACE_ALL:
            case SWAP_WITH_CURSOR: {
                // Allow dropping a skill into the skills bar
                if (clickSkillSlot) {
                    iClick.setCancelled(false);
                    break;
                }

                // Allow 'deleting' a skill into the book
                if (topInventory) {
                    iClick.setCancelled(false);
                    iClick.setCurrentItem(null);

                    // When SWAP_WITH_CURSOR, is used, reset the inventory
                    resetInventoryLater(iClick.getView().getTopInventory(), event.getMenu().getContents());
                    break;
                }
            }

        }
    }

    private void resetInventoryLater(final Inventory inventory, final ItemStack[] contents) {
        // Reset the book inventory
        new BukkitRunnable() {

            @Override
            public void run() {
                inventory.setContents(contents);
            }

        }.runTaskLater(plugin, 1);
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
