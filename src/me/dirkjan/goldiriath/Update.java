package me.dirkjan.goldiriath;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;

public class Update {
    public final Goldiriath plugin;

    public Update(Goldiriath plugin) {
        this.plugin = plugin;
    }
    
    public int updateLevel(Player player){
        return plugin.pm.getData(player).calculateLevel();
    }
    
    public void updateScoreboard(Player player){
        
        plugin.pm.getData(player).getSidebar().getScoreboard().resetScores("money");
        int money = plugin.pm.getData(player).getMoney();
        Score moneyscore = plugin.pm.getData(player).getSidebar().getScore("money " + ChatColor.GOLD + money);
        moneyscore.setScore(1);
        
         plugin.pm.getData(player).getSidebar().getScoreboard().resetScores("xp");
         int xp = plugin.pm.getData(player).getXp();
         int nextxp = plugin.pm.getData(player).calculatenextxp();
         Score xpscore = plugin.pm.getData(player).getSidebar().getScore("xp " + ChatColor.LIGHT_PURPLE + xp + "/" + nextxp);
         xpscore.setScore(2);
         
         plugin.pm.getData(player).getSidebar().getScoreboard().resetScores("level");
         int level = updateLevel(player);
         Score levelScore = plugin.pm.getData(player).getSidebar().getScore("level " + ChatColor.DARK_GREEN + level);
         levelScore.setScore(3);
         
         plugin.pm.getData(player).getSidebar().getScoreboard().resetScores("health");
         int health = plugin.pm.getData(player).getHealth();
         int maxhealth = plugin.pm.getData(player).getMaxHealth();
         Score healthScore = plugin.pm.getData(player).getSidebar().getScore("health " + ChatColor.RED + health + "/" + maxhealth);
         healthScore.setScore(4);
         
         plugin.pm.getData(player).getSidebar().getScoreboard().resetScores("mana");
         int mana = plugin.pm.getData(player).getMana();
         int maxmana = plugin.pm.getData(player).getMaxMana();
         Score manaScore = plugin.pm.getData(player).getSidebar().getScore("mana " + ChatColor.BLUE + mana + "/" + maxmana);
         manaScore.setScore(5);
    }

}