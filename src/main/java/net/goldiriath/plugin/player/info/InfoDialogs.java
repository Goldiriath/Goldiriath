package net.goldiriath.plugin.player.info;

import lombok.Getter;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.questing.dialog.Dialog;
import net.goldiriath.plugin.questing.dialog.OptionSet;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.questing.script.Script;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InfoDialogs extends Info {

    @Getter
    private OptionSet currentOption;
    private BukkitTask currentOptionTimeout;
    private Script.ScriptTask dialogTask;

    public InfoDialogs(PlayerData data) {
        super(data);
    }

    public boolean isShowingOption() {
        return getCurrentOption() != null;
    }

    public void showOption(final OptionSet option) {
        final Player player = data.getPlayer();

        endOption();
        this.currentOption = option;
        option.getMessage().send(player);

        this.currentOptionTimeout = new BukkitRunnable() {
            @Override
            public void run() {
                // TODO improve?
                if (getCurrentOption().equals(option)) {
                    endOption();
                }
                player.sendMessage(ChatColor.YELLOW + "Note" + ChatColor.WHITE + ": You've stopped speaking to this character.");
                endOption();
            }
        }.runTaskLater(plugin, plugin.config.getInt(ConfigPaths.DIALOG_TIMEOUT));
    }

    public void endOption() {
        this.currentOption = null;
        try {
            this.currentOptionTimeout.cancel();
        } catch (Exception ignored) {
        } finally {
            this.currentOptionTimeout = null;
        }
    }

    public void endDialog() {
        this.dialogTask.cancel();
        this.dialogTask = null;
    }

    public void start(Dialog dialog) {
        if (isInDialog()) {
            endDialog();
        }

        final Player player = data.getPlayer();
        this.dialogTask = dialog.getScript().execute(player);
    }

    public boolean isInDialog() {
        return dialogTask != null && dialogTask.isRunning();
    }

}
