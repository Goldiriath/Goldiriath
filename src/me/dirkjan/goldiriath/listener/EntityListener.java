package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.mobspawn.MobSpawn;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityListener extends RegistrableListener {

    public EntityListener(Goldiriath plugin) {
        super(plugin);
    }

    public void onEntityDamagByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Monster && event.getDamager() instanceof Player) {
            Entity damaged = event.getEntity();
            Player damager = (Player) event.getDamager();
            if (damaged.isDead()){
                int xp = 0;
                MobSpawn mobspawn = (MobSpawn) damaged.getMetadata("mobspawn");
                int moblevel = mobspawn.getProfile().getLevel();
                int playerlevel = plugin.pm.getData(damager).calculateLevel();
                double diff = Math.abs(playerlevel - moblevel);
                if (Math.abs(diff) <= 1){
                    xp = 5;
                }
                if (diff >= 2 && diff <=3 && moblevel >= playerlevel){
                    xp = 7;
                }
                if (diff >= 2 && diff <=3 && playerlevel >= moblevel){
                    xp = 2;
                }
                plugin.pm.getData(damager).addXP(xp);
            }
                
            
        }

    }
}
