package net.goldiriath.plugin.game.damage;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.game.mobspawn.citizens.HostileMobTrait;
import net.goldiriath.plugin.math.DamageMath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AttackManager extends AbstractService {

    public AttackManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    // For bows, wands
    public void attack(Player attacker, ItemStack stack, Entity e, Modifier... modifiers) {
        GItemMeta m = plugin.im.getMeta(stack, false);

        if (m == null) {
            logger.warning(attacker.getName() + " tried to attack entity " + e.getEntityId() + " with an item without item meta!");
            return;
        }

        double baseDamage = DamageMath.baseDamage(m);
        double attackDamage = DamageMath.attackDamage(baseDamage, attacker, modifiers);

        attack(attacker, e, attackDamage);
    }

    public void attack(Entity attacker, Entity defender, double attackDamage) {
        // TODO: Armor
        effective(attacker, defender, attackDamage);
    }

    public void effective(Entity attacker, Entity defender, double effectiveDamage) {
        /// TODO: Inflict the damage
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityHitNpc(NPCDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getDamager() instanceof Arrow && event.getDamager() instanceof Egg)) {
            return;
        }

        HostileMobTrait trait = event.getNPC().getTrait(HostileMobTrait.class);
        if (trait == null) {
            return;
        }

        Entity damager = event.getDamager();
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }
            damager = (Entity) arrow.getShooter();
        }
        if (damager instanceof Egg) {
            Egg egg = (Egg) damager;
            if (!(egg.getShooter() instanceof Player)) {
                return;
            }
            damager = (Entity) egg.getShooter();
        }
        Player player = (Player) damager;
        NPC damaged = event.getNPC();
        if (!(damaged.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity = (LivingEntity) damaged.getEntity();
        ItemStack npcArmor[] = entity.getEquipment().getArmorContents();
        double damage = DamageMath.effectiveDamage(player.getItemInHand(), npcArmor[0], npcArmor[1], npcArmor[2], npcArmor[3]);;

        boolean alive = trait.inflict(player, (int) damage);

        if (!alive) {
            // DIE DIE DIE
            event.getNPC().destroy();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onNPCHitPlayer(NPCDamageEntityEvent event) {
        if (!(event.getDamaged() instanceof Player)) {
            return;
        }

        NPC npcDamager = event.getNPC();
        if (!(npcDamager.getEntity() instanceof LivingEntity && npcDamager.getEntity() instanceof Arrow && npcDamager.getEntity() instanceof Egg)) {
            return;
        }
        Entity damager = (Entity) event.getNPC();
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }
            damager = (Entity) arrow.getShooter();
        }
        if (damager instanceof Egg) {
            Egg egg = (Egg) damager;
            if (!(egg.getShooter() instanceof Player)) {
                return;
            }
            damager = (Entity) egg.getShooter();
        }
        LivingEntity entity = (LivingEntity) damager;
        Player player = (Player) event.getDamaged();
        ItemStack playerArmor[] = player.getEquipment().getArmorContents();
        double damage = DamageMath.effectiveDamage(entity.getEquipment().getItemInHand(), playerArmor[0], playerArmor[1], playerArmor[2], playerArmor[3]);

        event.setDamage((int) damage);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void handlePvp(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        // Disable PvP for now.
        event.setCancelled(true);
        ((Player) event.getDamager()).sendMessage(ChatColor.RED + "PvP is disabled in this area.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getItemInHand().getType() != Material.EMERALD) {
            return;
        }

        player.launchProjectile(Egg.class);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG && event.getEntityType() == EntityType.CHICKEN) {
            event.setCancelled(true);
        }
    }

}
