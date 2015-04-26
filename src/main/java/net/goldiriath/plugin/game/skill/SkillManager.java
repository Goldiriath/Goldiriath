package net.goldiriath.plugin.game.skill;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryManager;
import net.goldiriath.plugin.game.skill.menu.WeaponMenu;
import net.goldiriath.plugin.game.skill.type.ActiveSkill;
import net.goldiriath.plugin.game.skill.type.Skill;
import net.goldiriath.plugin.player.data.DataSkills;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class SkillManager extends AbstractService {

    public SkillManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    // ignoreCancelled = false: see documentation
    @EventHandler(ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack stack = player.getEquipment().getItemInMainHand();

        if (stack == null) {
            return;
        }
        
        // Spell book
        if (stack.equals(InventoryManager.SPELL_BOOK)) {
            new WeaponMenu(plugin).openMenu(player);
            return;
        }

        DataSkills data = plugin.pm.getData(player).getSkills();

        SkillType type = SkillType.fromDisplay(stack);
        if (type == null) {
            return;
        }
        
        if (!data.getSkills().containsKey(type)) {
            player.sendMessage(ChatColor.RED + "You haven't learnt that spell, so you cannot use it.");
            return;
        }
        
        event.setCancelled(true);
        useSkill(player, data.getSkills().get(type));
    }

    public void useSkill(Player player, Skill skill) {
        if (!(skill instanceof ActiveSkill)) {
            player.sendMessage(ChatColor.GOLD + "You cannot use a passive skill.");
            return;
        }

        ActiveSkill active = (ActiveSkill) skill;
        active.use();
        player.sendMessage(ChatColor.GREEN + "You used " + active.getType().getName());
    }

}
