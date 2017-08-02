package net.goldiriath.plugin.game.skill;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.inventory.SlotType;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.skill.menu.WeaponMenu;
import net.goldiriath.plugin.game.skill.type.ActiveSkill;
import net.goldiriath.plugin.game.skill.type.Skill;
import net.goldiriath.plugin.game.skill.type.WeaponType;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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
    }

    public void useSkill(Player player, Skill skill) {
        if (!(skill instanceof ActiveSkill)) {
            player.sendMessage(ChatColor.GOLD + "You cannot use a passive skill.");
            return;
        }

        // Stops the player from using skills in its hotbar that do no correspond to its weapon
        SkillType type = skill.getType();
        if (!type.getWeapon().equals(InventoryUtil.getWeaponType(InventoryUtil.getWeapon(player)))) {
            player.sendMessage(ChatColor.RED + "You cannot use this skill with your equiped weapon!");
            return;
        }

        // Checks if the player has enough mana, and updates the players mana.
        PlayerData data = plugin.pm.getData(player);
        if (data.getMana() < skill.getType().getManaCost()) {
            player.sendMessage(ChatColor.RED + "You do not have enough mana to use this skill!");
            return;
        }

        // TODO: Fix this when mana is implemented.
        data.setMana(100);

        // Checks if the skill is on cooldown.
        if (System.nanoTime() - skill.getLastUse() < (long) skill.getType().getDelayTicks() * 50000000 ) {
            player.sendMessage(ChatColor.GOLD + "This Skill is still on cooldown!");
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack usedSkill = inventory.getItem(inventory.first(skill.getType().getDisplay().getStack()));
        usedSkill.setAmount(skill.getType().getDelayTicks()/20);

        putSkillOnCooldown(player, inventory, InventoryUtil.firstSimilar(inventory, usedSkill), skill.getType());

        ActiveSkill active = (ActiveSkill) skill;
        active.use();
        active.setLastUse(System.nanoTime());
    }

    public void setSkillLevel(Player player, SkillType type, int level) {
        DataSkills data = plugin.pm.getData(player).getSkills();

        if (level <= 0) {
            data.getSkills().remove(type);
            plugin.logger.info("Removed " + player.getName() + "'s " + type.getName() + " skill");
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
        plugin.logger.info("Set " + player.getName() + "'s " + type.getName() + " skill level to " + level);
    }

    private BukkitTask putSkillOnCooldown(final Player player, final Inventory inventory, final int pos, SkillType type) {
        // Reset a specific slot in a players inventory.

        return new BukkitRunnable() {

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                inventory.getItem(pos).setAmount(inventory.getItem(pos).getAmount() - 1);
                if(inventory.getItem(pos).getAmount() == 1) {
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 20, 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        ItemStack[] contents = inventory.getContents();

        int[] skillSlots = SlotType.SKILL.getIndices();

        for (int i = 0; i < skillSlots.length; i++) {
            int index = skillSlots[i];
            ItemStack stack = contents[index];
            if (InventoryUtil.isEmpty(stack)
                    || stack.getAmount() == 1) {
                continue;
            }

            stack.setAmount(1);
        }

        // Ensure a skillbook is present
        inventory.setItem(SlotType.SKILL_BOOK.getIndices()[0], StaticItem.SKILL_BOOK.getStack());
    }

    @EventHandler(ignoreCancelled = true)
    public void activateSkill(PlayerItemHeldEvent event) {
        if(event.getPreviousSlot() == 0 && event.getNewSlot() > 0 && event.getNewSlot() < 5) {
            event.setCancelled(true);

            PlayerInventory inventory = event.getPlayer().getInventory();
            PlayerData data = plugin.pm.getData(event.getPlayer());
            ItemStack stack = inventory.getItem(event.getNewSlot());

            if(stack != null) {
                Skill usedSkill = data.getSkills().getSkills().get(InventoryUtil.getSkill(stack));
                useSkill(event.getPlayer(), usedSkill);
            }
        }
    }

}
