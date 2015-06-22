package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener extends RegistrableListener {

    public EntityListener(Goldiriath plugin) {
        super(plugin);
    }

    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)){
            return;
        }
        EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        if(!(newEvent.getDamager() instanceof Player)){
            return;
        }
        plugin.pm.getPlayer((Player)newEvent.getDamager()).recordKill(event.getEntity());

    }
}
