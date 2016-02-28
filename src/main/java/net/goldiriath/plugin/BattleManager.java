package net.goldiriath.plugin;

import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEntityEvent;
import net.goldiriath.plugin.mobspawn.citizens.HostileMobTrait;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class BattleManager extends AbstractService {

    public BattleManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
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

}
