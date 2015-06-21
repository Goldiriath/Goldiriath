package me.dirkjan.goldiriath.dialog.script;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.Service;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ScriptRunner extends BukkitRunnable implements Service {

    private final Goldiriath plugin;
    private final Script script;
    private final Player player;
    //
    private BukkitTask task;
    private int index = 0;
    private int tick = -1;

    public ScriptRunner(Script script, Player player) {
        this.plugin = script.getDialog().getHandler().getManager().getPlugin();
        this.script = script;
        this.player = player;
    }

    @Override
    public void start() {
        index = 0;
        tick = -1; // Start at tick -1 so that 0-delays are ran properly
        task = super.runTaskTimer(plugin, 0, 1);
    }

    public boolean isRunning() {
        return task != null;
    }

    @Override
    public void stop() {
        try {
            task.cancel();
        } finally {
            task = null;
        }
    }

    @Override
    @Deprecated
    public void run() {
        if (task == null) {
            throw new IllegalStateException("run() shouldn't be called manually");
        }

        if (!player.isOnline()) {
            stop();
            return;
        }

        // Next tick
        tick++;

        if (index >= script.size()) {
            stop(); // Dialog ended
            return;
        }

        final ScriptItem item = script.get(index);

        if (item.getDelay() <= tick) {
            return; // Wait until we can run the next script item
        }

        // Execute script item
        item.execute(player);

        // Prep next script item
        tick = -1;
        index++;
    }

    public Player getPlayer() {
        return player;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Script getScript() {
        return script;
    }

}
