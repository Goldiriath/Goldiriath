package me.dirkjan.goldiriath.player;

import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.dialog.Dialog;
import me.dirkjan.goldiriath.dialog.OptionSet;
import me.dirkjan.goldiriath.dialog.script.ScriptRunner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class GPlayer {

    private final Goldiriath plugin;
    private final PlayerManager manager;
    private final Player player;
    private final PlayerData data;
    //
    private final Objective sidebar;
    private OptionSet currentOption;
    private BukkitTask currentOptionTimeout;
    private ScriptRunner scriptRunner;

    public GPlayer(PlayerManager manager, Player player) {
        this.plugin = manager.getPlugin();
        this.manager = manager;
        this.player = player;
        this.data = new PlayerData(this);
        //
        this.sidebar = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplayName("Statistics");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(sidebar.getScoreboard());
    }

    public PlayerManager getManager() {
        return manager;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getData() {
        return data;
    }

    public void update() {
        sidebar.getScoreboard().resetScores("money");
        int money = data.getMoney();
        Score score = sidebar.getScore("money " + ChatColor.GOLD + money);
        score.setScore(1);
    }

    //
    //
    //
    public OptionSet getCurrentOption() {
        return currentOption;
    }

    public boolean isShowingOption() {
        return getCurrentOption() != null;
    }

    public void showOption(final OptionSet option) {
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
        }.runTaskLater(manager.getPlugin(), manager.getPlugin().config.getInt(ConfigPaths.DIALOG_TIMEOUT));
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
        this.scriptRunner.stop();
        this.scriptRunner = null;
    }

    public void startDialog(Dialog dialog) {
        if (scriptRunner != null) {
            endDialog();
        }

        data.recordDialog(dialog.getId());

        final ScriptRunner sr = new ScriptRunner(dialog.getScript(), player);
        sr.start();
        this.scriptRunner = sr;
    }

    public boolean isInDialog() {
        return getScriptRunner() != null;
    }

    public ScriptRunner getScriptRunner() {
        if (scriptRunner != null && !scriptRunner.isRunning()) {
            scriptRunner = null;
        }

        return scriptRunner;
    }

}
