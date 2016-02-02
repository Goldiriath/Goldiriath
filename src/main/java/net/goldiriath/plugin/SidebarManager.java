package net.goldiriath.plugin;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import net.goldiriath.plugin.math.XPMath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.InfoSidebar;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SidebarManager extends AbstractService {

    public static long SIDEBAR_UPDATE_SECONDS = 20L;
    public static long UPDATER_TICKS = 5L * 20L;
    //
    @Getter
    private BukkitTask task;

    public SidebarManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        task = plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                new SideBarUpdater(),
                UPDATER_TICKS,
                UPDATER_TICKS);
    }

    @Override
    protected void onStop() {
        Util.cancel(task);
        task = null;
    }

    public void update(PlayerData data) {
        List<String> lines = Lists.newArrayList();

        int xp = data.getXp();
        int level = XPMath.xpToLevel(xp);

        lines.add("money " + ChatColor.GOLD + data.getMoney());
        lines.add("xp " + ChatColor.LIGHT_PURPLE + xp + "/" + XPMath.xpToNextXp(xp));
        lines.add("level " + ChatColor.DARK_GREEN + level);
        lines.add("health " + ChatColor.RED + data.getHealth() + "/" + data.getMaxHealth());
        lines.add("mana " + ChatColor.BLUE + data.getMana() + "/" + data.getMana());

        data.getSidebar().setData(lines);
    }

    public class SideBarUpdater implements Runnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData data = Goldiriath.plugin.pm.getData(player);
                InfoSidebar sb = data.getSidebar();

                // Apply threshold
                long currentTime = TimeUtils.getUnix();
                if (sb.getLastUpdate() + SIDEBAR_UPDATE_SECONDS > currentTime) {
                    continue;
                }
                sb.setLastUpdate(currentTime);

                update(data);
            }
        }

    }

}
