package net.goldiriath.plugin;

import net.goldiriath.plugin.util.service.Service;
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
        if (isStarted()) {
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 1, 1);
    }

    @Override
    public void stop() {
        try {
            task.cancel();
        } catch (Exception ignored) {
        } finally {
            task = null;
        }
    }

    @Override
    public boolean isStarted() {
        return task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId());
    }

}
