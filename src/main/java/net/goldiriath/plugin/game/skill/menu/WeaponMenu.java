package net.goldiriath.plugin.game.skill.menu;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.type.WeaponType;
import net.goldiriath.plugin.util.IconMenu;
import net.goldiriath.plugin.util.IconMenu.OptionClickEvent;
import net.goldiriath.plugin.util.IconMenu.OptionClickEventHandler;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

public class WeaponMenu extends PluginComponent<Goldiriath> implements OptionClickEventHandler {

    public WeaponMenu(Goldiriath plugin) {
        super(plugin);
    }

    public void openMenu(Player player) {
        final IconMenu menu = new IconMenu("Skills", 9, this, plugin);

        menu.setOption(0, StaticItem.MENU_SKILL_SWORD.getStack(), "Sword");
        menu.setOption(1, StaticItem.MENU_SKILL_BOW.getStack(), "Bow");
        menu.setOption(2, StaticItem.MENU_SKILL_KNIFE.getStack(), "Knife");
        menu.setOption(3, StaticItem.MENU_SKILL_WAND.getStack(), "Wand");
        menu.setOption(8, StaticItem.MENU_DONE.getStack(), "Done");
        menu.open(player);
    }

    @Override
    public void onOptionClick(OptionClickEvent event) {

        if (event.getName().equals("done")) {
            event.setWillClose(true);
            event.setWillDestroy(true);
            return;
        }

        for (WeaponType type : WeaponType.values()) {
            if (type.name().equalsIgnoreCase(event.getName())) {
                event.setWillDestroy(true);
                new WeaponSkillMenu(plugin).openMenu(event.getPlayer(), type);
                break;
            }
        }
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
