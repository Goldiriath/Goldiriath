package net.goldiriath.plugin.game.damage;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEntityEvent;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.damage.modifier.Modifier;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.game.mobspawn.citizens.HostileMobTrait;
import net.goldiriath.plugin.math.DamageMath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    public void onPlayerHitNpc(NPCDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        HostileMobTrait trait = event.getNPC().getTrait(HostileMobTrait.class);
        if (trait == null) {
            return;
        }

        Player player = (Player) event.getDamager();
        double damage = event.getDamage();

        // TODO: calculate armor and weapon damage modifiers, etc
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

        Player player = (Player) event.getDamaged();
        double damage = event.getDamage();

        // TODO: calculate armor and weapon damage modifiers, etc
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

}
