package net.goldiriath.plugin;

import net.goldiriath.plugin.math.XPMath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class GUIManager extends AbstractService {

    private GUIUpdater guiUpdater;
    private BukkitTask updateTask;

    public GUIManager(Goldiriath plugin) {
        super(plugin);
        guiUpdater = new GUIUpdater();
    }

    public void updateGUI(Player player) {
        PlayerData playerData = plugin.pm.getData(player);
        updateMana(player, playerData.getMaxMana(), playerData.getMana());
        updateXP(player, playerData.getXp());
    }

    private void updateMana(Player player, int maxMana, int currentMana) {
        float manaPerCrystal = maxMana / 20;
        int manaCrystals = Math.round(currentMana / manaPerCrystal);
        player.setFoodLevel(manaCrystals);
    }

    private void updateXP(Player player, int xp) {
        int level = XPMath.xpToLevel(xp);
        int currentXp = XPMath.xpToLevelXp(xp);
        int neededXp = XPMath.levelToNextXp(level + 1);
        double xpPercentage = currentXp / neededXp;
        player.setLevel(level);
        int xpInMCLevel = player.getExpToLevel();
        player.giveExp((int) (xpInMCLevel * xpPercentage));
    }

    @Override
    protected void onStart() {
        updateTask = plugin.getServer().getScheduler().runTaskTimer(plugin, guiUpdater, 20, 20);
    }

    @Override
    protected void onStop() {
        Util.cancel(updateTask);
        updateTask = null;
    }

    public class GUIUpdater implements Runnable {

        @Override
        public void run() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                plugin.gm.updateGUI(player);
            }
        }
    }
}
