package net.goldiriath.plugin.game.skill;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryManager;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.skill.menu.WeaponMenu;
import net.goldiriath.plugin.game.skill.type.ActiveSkill;
import net.goldiriath.plugin.game.skill.type.Skill;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.data.DataSkills;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

        // TODO: look at being able to use skillbook on blocks.

        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack stack = player.getEquipment().getItemInMainHand();

        if (stack == null) {
            return;
        }

        // Spell book
        if (InventoryUtil.isSkillBook(stack)) {
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

        // Checks if the player has enough mana, and updates the players mana.
        PlayerData data = plugin.pm.getData(player);
        if(data.getMana() < skill.getType().getManaCost()) {
            player.sendMessage(ChatColor.RED + "You do not have enough mana to use this skill!");
            return;
        }
        // TODO: fix this when mana is implemented.
        data.setMana(100);

        // Checks if the skill is on cooldown.
        if(System.nanoTime() - skill.getLastUse() < (long) skill.getType().getDelayTicks() * 50000000 ) {
            // TODO: Implement a better way to show that a skill is on cooldown!
            player.sendMessage(ChatColor.GOLD + "This Skill is still on cooldown!");
            return;
        }

        ActiveSkill active = (ActiveSkill) skill;
        active.use();
        active.setLastUse(System.nanoTime());
    }

    public void setSkillLevel(Player player, SkillType type, int level) {
        DataSkills data = plugin.pm.getData(player).getSkills();

        if (level <= 0) {
            data.getSkills().remove(type);
            plugin.logger.info("Removed " + player.getName() + "'s " + type.getName() + " skill");
            //player.sendMessage(ChatColor.DARK_GREEN + "Removed " + player.getName() + "'s " + type.getName() + " skill");
            return;
        }

        Skill skill;
        if (data.getSkills().containsKey(type)) {
            skill = data.getSkills().get(type);
        } else {
            skill = type.create(player, new SkillMeta(type));
            data.getSkills().put(type, skill);
        }

        skill.getMeta().level = level;
       // player.sendMessage(ChatColor.DARK_GREEN + "Set " + player.getName() + "'s " + type.getName() + " skill level to " + level);
        plugin.logger.info("Set " + player.getName() + "'s " + type.getName() + " skill level to " + level);
    }

}
