package net.goldiriath.plugin.game;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.info.modifier.Effect;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EffectsTicker extends AbstractService {

    private BukkitTask tickTask;

    public EffectsTicker(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        tickTask = new BukkitRunnable() {

            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @Override
    protected void onStop() {
        Util.cancel(tickTask);
        tickTask = null;
    }

    public void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            for (Effect m : plugin.pym.getData(player).getModifiers().getActiveModifiers()) {
                m.tick(player);
            }
        }
    }

}
