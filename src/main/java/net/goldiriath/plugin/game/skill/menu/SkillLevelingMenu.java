package net.goldiriath.plugin.game.skill.menu;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.SkillType;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.List;

public class SkillLevelingMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private final SkillType type;

    public SkillLevelingMenu(Goldiriath plugin, SkillType type) {
        super(plugin);
        this.type = type;
    }

    public void openMenu(Player player) {
        final IconMenu menu = new IconMenu("Do you want to unlock this skill?", 9, this, plugin);

        menu.setOption(0, StaticItem.MENU_DONE.getStack(), "done");
        menu.setOption(8, StaticItem.MENU_CANCEL.getStack(), "cancel");
        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        if (!event.getName().equals("done")) {
            event.setWillClose(true);
            event.setWillDestroy(true);
            return;
        }

        PlayerData data = plugin.pm.getData(event.getPlayer());

        List<SkillType> weaponSkills = SkillType.findForWeapon(type.getWeapon());
        List<SkillType> unlockedSkills = new ArrayList<>();

        for(int i = 0; i < weaponSkills.size(); i++) {
            if(data.getSkills().getSkills().containsKey(weaponSkills.get(i))) {
                unlockedSkills.add(weaponSkills.get(i));
            }
        }

        if(unlockedSkills.size() < type.getReqSkills()) {
            event.getPlayer().sendMessage(ChatColor.RED + "You have " + unlockedSkills.size() + " and need " + type.getReqSkills() + " unlocked " + type.getWeapon() + " skill(s) to unlock " + type.getName());
            event.setWillClose(true);
            event.setWillDestroy(true);
            return;
        }

        if(data.getSkillPoints() < 1) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have a free Skill Point to level this skill with!");
            event.setWillClose(true);
            event.setWillDestroy(true);
            return;
        } else {
            // Removes one SkillPoint from the player to level the skill.
            data.setSkillPoints(data.getSkillPoints()-1);
        }

        plugin.sm.setSkillLevel(event.getPlayer(), type, 1);

        event.setWillClose(true);
        event.setWillDestroy(true);
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}

