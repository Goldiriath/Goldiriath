package me.dirkjan.goldiriath;

import me.dirkjan.goldiriath.util.Service;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class HeartBeat implements Service, Runnable {

    private final Goldiriath plugin;
    //
    private BukkitTask task;

    public HeartBeat(Goldiriath plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        // Update all players
        plugin.pm.updateAll();

    }

    @Override
    public void start() {
        if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 1, 1);
    }

    @Override
    public void stop() {
        try {
            task.cancel();
        } finally {
            task = null;
        }
    }

}
