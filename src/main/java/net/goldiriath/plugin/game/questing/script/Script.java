package net.goldiriath.plugin.game.questing.script;

import net.goldiriath.plugin.game.questing.script.item.ScriptItem;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.SafeArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Script extends SafeArrayList<ScriptItem> {

    private static final long serialVersionUID = -275727347234888L;

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final ScriptContext context;

    public Script(Goldiriath plugin) {
        this(plugin, new ScriptContext());
    }

    public Script(Goldiriath plugin, ScriptContext context) {
        this.plugin = plugin;
        this.context = context;
    }

    public ScriptTask execute(Player player) {
        return new ScriptTask(player);
    }

    public class ScriptTask {

        private final Player player;
        private BukkitTask next;

        ScriptTask(Player player) {
            this.player = player;
            schedule(0);
        }

        final void schedule(final int offset) {
            if (offset >= size()) {
                next = null;
                return;
            }

            this.next = new BukkitRunnable() {

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        return;
                    }

                    get(offset).execute(player);

                    if (offset >= size()) {
                        next = null;
                        return;
                    }
                    schedule(offset + 1);
                }

            }.runTaskLater(plugin, get(offset).getDelay());
        }

        public void cancel() {
            try {
                next.cancel();
            } catch (Exception ignored) {
            } finally {
                next = null;
            }
        }

        public boolean isRunning() {
            return next != null;
        }
    }

}
