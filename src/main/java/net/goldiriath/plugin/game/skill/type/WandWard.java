package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.inventory.SlotType;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.player.info.InfoWand;
import net.goldiriath.plugin.util.IconMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koen on 03/08/2017.
 */
public class WandWard extends ActiveSkill {

    public WandWard(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        InfoWand infoWand = plugin.pm.getData(player).getWand();
        if (infoWand.isChoosing()) return;
        StaticItem[] elements = {
                StaticItem.SKILL_WAND_WARD_FIRE,
                StaticItem.SKILL_WAND_WARD_WATER,
                StaticItem.SKILL_WAND_WARD_EARTH,
                StaticItem.SKILL_WAND_WARD_AIR,
        };
        ItemStack[] skillSlots = new ItemStack[4];
        int ele = 0;
        for (int i : SlotType.SKILL.getIndices()) {
            skillSlots[ele] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, elements[ele++].getStack());
        }
        if (skillSlots.length != 4) {
            plugin.logger.severe("Could not store all items for element choosing ritual (WandWard skill)");
            return;
        }
        infoWand.setStoredItems(skillSlots);
        infoWand.setChoosing(true);
    }


}
