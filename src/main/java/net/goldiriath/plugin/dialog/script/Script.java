package net.goldiriath.plugin.dialog.script;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.dialog.Dialog;
import net.goldiriath.plugin.quest.action.Action;
import net.goldiriath.plugin.util.SafeArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Script extends SafeArrayList<ScriptItem> implements Action {

    private static final long serialVersionUID = 1L;

    private final Goldiriath plugin;
    private final Dialog dialog;

    public Script(Dialog dialog) {
        this.plugin = dialog.getHandler().getManager().getPlugin();
        this.dialog = dialog;
    }

    public Dialog getDialog() {
        return dialog;
    }

    @Override
    public void execute(Player player) {
        execute(player, 0);
    }

    // Exceutes the script items in this script in order, with proper delay
    public void execute(final Player player, final int offset) {
        if (offset > size()) {
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!player.isOnline()) {
                    return;
                }

                get(offset).execute(player);

                execute(player, offset + 1);
            }

        }.runTaskLater(plugin, get(offset).getDelay());
    }

}
