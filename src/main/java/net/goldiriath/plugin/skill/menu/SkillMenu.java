package net.goldiriath.plugin.skill.menu;

import java.util.ArrayList;
import java.util.List;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.skill.SkillType;
import net.goldiriath.plugin.util.IconMenu;
import net.goldiriath.plugin.util.IconMenu.OptionClickEvent;
import net.goldiriath.plugin.util.IconMenu.OptionClickEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkillMenu {

    private static final ItemStack UNLEARNED_SKILL_MATERIAL = new ItemStack(Material.THIN_GLASS, 1, DyeColor.BLACK.getDyeData());
    private Goldiriath plugin;

    public SkillMenu(Goldiriath plugin) {
        this.plugin = plugin;
    }

    private void openSubMenu(Player player, SkillType base) {
        // TODO
    }

    public void openMenu(Player player) {
        final IconMenu menu = new IconMenu("Skills", 9, new OptionClickEventHandler() {
            @Override
            public void onOptionClick(OptionClickEvent event) {
                event.getPlayer().sendMessage("You have chosen " + event.getName());

                for (SkillType type : SkillType.values()) {
                    if (type.getName().equals(event.getName())) {
                        openSubMenu(event.getPlayer(), type);
                        break;
                    }
                }

                event.setWillClose(true);
            }
        }, plugin);

        final List<SkillType> baseSkills = new ArrayList<>();
        for (SkillType type : SkillType.values()) {
            if (type.getReqSkill() == null) {
                baseSkills.add(type);
            }
        }

        int size = baseSkills.size() * (baseSkills.size() - 1);
        int offset = (9 - size) / 2;

        int i = offset;
        for (SkillType baseSkill : baseSkills) {
            if (plugin.pm.getData(player).hasSkill(baseSkill)) {
                menu.setOption(i, new ItemStack(baseSkill.getDisplay(), 1), baseSkill.getName(), ChatColor.GREEN + "Learned");
            } else {
                menu.setOption(i, UNLEARNED_SKILL_MATERIAL, baseSkill.getName(), ChatColor.RED + "Unlearned Skill");
            }

            i += 2;
        }
        menu.open(player);
    }

}
