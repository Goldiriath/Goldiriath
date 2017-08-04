package net.goldiriath.plugin.game.damage;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.game.mobspawn.citizens.HostileMobTrait;
import net.goldiriath.plugin.math.DamageMath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class DamageManager extends AbstractService {

    private final AutoHealer autoHealer;
    private BukkitTask healTask;

    public DamageManager(Goldiriath plugin) {
        super(plugin);
        this.autoHealer = new AutoHealer();
    }

    @Override
    protected void onStart() {
        healTask = plugin.getServer().getScheduler().runTaskTimer(plugin, autoHealer, 20, 20);
    }

    @Override
    protected void onStop() {
        Util.cancel(healTask);
        healTask = null;
    }

    public void attack(Entity attacker, ItemStack stack, Entity e, Modifier... modifiers) {
        GItemMeta m = plugin.im.getMeta(stack, false);

        if (m == null) {
            logger.warning(attacker.getName() + " tried to attack entity " + e.getEntityId() + " with an item without item meta!");
            return;
        }

        double baseDamage = DamageMath.baseDamage(m);
        double attackDamage = DamageMath.attackDamage(baseDamage, attacker, modifiers);

        attack(attacker, attackDamage, e);
    }

    public void attack(Entity attacker, double attackDamage, Entity defender) {
        // TODO: Armor
        effective(attacker, attackDamage, defender);
    }

    public void attack(Player player, double attackDamage) {
        // TODO: Armor
        effectiveOnPlayer(attackDamage, player);
    }

    public void effective(Entity attacker, double effectiveDamage, Entity defender) {
        if (attacker instanceof Player && isNPC(defender)) {
            int health = effectiveOnNPC((Player) attacker, effectiveDamage, ((NPCHolder) defender).getNPC());
            if (health > 0) {
                attacker.sendMessage(ChatColor.GOLD + "You hit the " + defender.getName() + ", dealing " + effectiveDamage + " damage.");
            } else {
                attacker.sendMessage(ChatColor.GOLD + "You killed the " + defender.getName() + ", dealing " + effectiveDamage + " damage.");
            }
            return;
        } else if (isNPC(attacker) && defender instanceof Player) {
            int health = effectiveOnPlayer(effectiveDamage, (Player) defender);
            if (health > 0) {
                defender.sendMessage(ChatColor.GOLD + "The " + attacker.getName() + " hit you, dealing " + effectiveDamage + " damage.");
            } else {
                defender.sendMessage(ChatColor.GOLD + "The " + attacker.getName() + " killed you, dealing " + effectiveDamage + " damage.");
            }
            return;
        }

        plugin.logger.warning("Unsupported attack: " + attacker.getType() + " tried to attack " + defender.getType());
    }

    public int effectiveOnNPC(Player attacker, double effectiveDamage, NPC defender) {

        HostileMobTrait defendTrait = defender.getTrait(HostileMobTrait.class);
        if (defendTrait == null) {
            return -1;
        }

        int damage = Math.max(1, (int) effectiveDamage);

        // HostileMobTrait keeps track of damage and health
        boolean alive = defendTrait.inflict(attacker, damage);

        if (alive) {
            return defendTrait.getHealth();
        }

        defender.despawn(DespawnReason.DEATH);
        defender.destroy();
        return 0;

    }

    public int effectiveOnPlayer(double effectiveDamage, Player defender) {
        PlayerData data = plugin.pm.getData(defender);

        int damage = Math.max(1, (int) effectiveDamage);
        int health = Math.min(0, data.getHealth() - damage);
        data.setHealth(health);

        return health;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityHitNpc(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
        if (npc.getTrait(HostileMobTrait.class) == null) {
            return;
        }

        Entity attacker = event.getDamager();

        // If we're hit by a projectile, resolve the shooter
        if (attacker instanceof Projectile) {
            Projectile proj = (Projectile) attacker;
            if (!(proj.getShooter() instanceof Entity)) {
                return;
            }
            attacker = (Entity) proj.getShooter();
        }

        if (!(attacker instanceof Player)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) attacker;
        ItemStack weapon = InventoryUtil.getWeapon(player);

        attack(attacker, weapon, npc.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onNPCHitEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Entity damager = (Entity) event.getDamager();
        if (damager instanceof Projectile) {
            Projectile proj = (Projectile) damager;
            if (!(proj.getShooter() instanceof Entity)) {
                return;
            }
            damager = (Entity) proj.getShooter();
        }

        if (!(damager instanceof NPCHolder)) {
            return;
        }

        NPC npc = ((NPCHolder) event).getNPC();

        HostileMobTrait trait = npc.getTrait(HostileMobTrait.class);
        if (trait == null) {
            return;
        }

        event.setCancelled(true);

        ItemStack hand = trait.getProfile().getHand();
        if (hand != null) {
            attack(damager, hand, event.getEntity());
        } else {
            attack(damager, trait.getProfile().getDamage(), event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerVsPlayer(EntityDamageByEntityEvent event) {
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        if (reason != SpawnReason.CUSTOM && reason != SpawnReason.DEFAULT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        plugin.logger.info("Cancelling player damage for " + event.getEntity().getName() + ": " + event.getCause());

        event.setCancelled(true);
    }

    private boolean isNPC(Entity entity) {
        return entity.hasMetadata("NPC");
    }

    public void heal(Player player, int amount) {
        PlayerData data = plugin.pm.getData(player);

        int health = data.getHealth() + amount;
        health = Math.min(data.getMaxHealth(), health);

        data.setHealth(health);
    }

    public class AutoHealer implements Runnable {

        @Override
        public void run() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                plugin.pm.getData(player).getBattle().autoHeal();
            }
        }

    }

}
