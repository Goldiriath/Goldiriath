package me.dirkjan.goldiriath.player;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.dirkjan.goldiriath.ConfigPaths;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.dialog.Dialog;
import me.dirkjan.goldiriath.dialog.OptionSet;
import me.dirkjan.goldiriath.dialog.script.ScriptRunner;
import me.dirkjan.goldiriath.mobspawn.MobSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class GPlayer {

    private final Goldiriath plugin;
    @Getter
    private final PlayerManager manager;
    @Getter
    private final Player player;
    @Getter
    private final PlayerData data;
    //
    @Getter
    private final Objective sidebar;
    //
    @Getter
    private OptionSet currentOption;
    private BukkitTask currentOptionTimeout;
    private ScriptRunner scriptRunner;
    //
    List<String> scoreList = new ArrayList<>();

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

    public void update() {

        for (String score : scoreList) {
            sidebar.getScoreboard().resetScores(score);
        }

        scoreList.clear();

        int money = data.getMoney();
        Score moneyscore = sidebar.getScore("money " + ChatColor.GOLD + money);
        moneyscore.setScore(1);
        scoreList.add("money " + ChatColor.GOLD + money);

        int xp = data.getXp();
        int nextxp = data.calculatenextxp();
        Score xpscore = sidebar.getScore("xp " + ChatColor.LIGHT_PURPLE + xp + "/" + nextxp);
        xpscore.setScore(2);
        scoreList.add("xp " + ChatColor.LIGHT_PURPLE + xp + "/" + nextxp);

        int level = data.calculateLevel();
        Score levelScore = sidebar.getScore("level " + ChatColor.DARK_GREEN + level);
        levelScore.setScore(3);
        scoreList.add("level " + ChatColor.DARK_GREEN + level);

        int health = data.getHealth();
        int maxhealth = data.getMaxHealth();
        Score healthScore = sidebar.getScore("health " + ChatColor.RED + health + "/" + maxhealth);
        healthScore.setScore(4);
        scoreList.add("health " + ChatColor.RED + health + "/" + maxhealth);

        int mana = data.getMana();
        int maxmana = data.getMaxMana();
        Score manaScore = sidebar.getScore("mana " + ChatColor.BLUE + mana + "/" + maxmana);
        manaScore.setScore(5);
        scoreList.add("mana " + ChatColor.BLUE + mana + "/" + maxmana);
    }

    //
    //
    //
    public void recordKill(LivingEntity killed) {
        int xp = 0;
        List<MetadataValue> metadataList = killed.getMetadata(MobSpawn.METADATA_ID);
        if (metadataList.isEmpty()) {
            return;
        }
        MobSpawn mobSpawn = (MobSpawn) metadataList.get(0).value();
        int moblevel = mobSpawn.getProfile().getLevel();
        int playerlevel = data.calculateLevel();
        double diff = Math.abs(playerlevel - moblevel);
        if (Math.abs(diff) <= 1) {
            xp = 5;
        }
        if (diff >= 2 && diff <= 3 && moblevel >= playerlevel) {
            xp = 7;
        }
        if (diff >= 2 && diff <= 3 && playerlevel >= moblevel) {
            xp = 2;
        }
        data.addXp(xp);
    }

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
