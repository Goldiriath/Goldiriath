package net.goldiriath.plugin.game.damage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Callback;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ArrowHitTracker extends AbstractService {

    private final Map<Arrow, Callback<Entity>> arrows = new HashMap<>();
    private BukkitTask dieTask;

    public ArrowHitTracker(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        dieTask = new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Arrow> it = arrows.keySet().iterator();
                while (it.hasNext()) {
                    Arrow a = it.next();
                    if (a.isValid() || a.isDead() || a.isOnGround()) {
                        it.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    @Override
    protected void onStop() {
        Util.cancel(dieTask);
        arrows.clear();
    }

    public void track(Arrow arrow, Callback<Entity> call) {
        arrows.put(arrow, call);
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow a = (Arrow) event.getDamager();

        if (!arrows.containsKey(a)) {
            return;
        }

        arrows.get(a).call(event.getEntity());
        arrows.remove(a);
    }

}
