package me.dirkjan.goldiriath;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

public class ScoreboardHandler {

    private final Goldiriath plugin;

    public ScoreboardHandler(Goldiriath plugin) {
        this.plugin = plugin;
    }

    public void updateMoney(Player player) {
        int money = plugin.pm.getData(player).getMoney();
        Score score = plugin.pm.getData(player).getSidebar().getScore("money " + ChatColor.GOLD + money);
        score.setScore(1);
    }

}
