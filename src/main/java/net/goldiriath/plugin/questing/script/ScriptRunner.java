package net.goldiriath.plugin.questing.script;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.script.item.ScriptItem;
import net.goldiriath.plugin.util.service.Service;
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
        this.plugin = script.getPlugin();
        this.script = script;
        this.player = player;
    }

    @Override
    public String getServiceId() {
        return "ScriptRunner-" + script.getContext().getId() + "-" + player.getName();
    }

    @Override
    public void start() {
        index = 0;
        tick = -1; // Start at tick -1 so that 0-delays are ran properly
        task = super.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public boolean isStarted() {
        return task != null;
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
