package me.dirkjan.goldiriath.dialog.script;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.dialog.Dialog;
import me.dirkjan.goldiriath.quest.action.Action;
import me.dirkjan.goldiriath.util.SafeArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Script extends SafeArrayList<ScriptItem> implements Action {

    private static final long serialVersionUID = 1L;

    private final Goldiriath plugin;
    private final Dialog dialog;

    public Script(Dialog dialog) {
        this.plugin = dialog.getDialogContainer().getManager().getPlugin();
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
