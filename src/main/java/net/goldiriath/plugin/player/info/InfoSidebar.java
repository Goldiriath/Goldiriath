package net.goldiriath.plugin.player.info;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class InfoSidebar extends Info {

    @Getter
    @Setter
    private long lastUpdate = 0;

    @Getter
    private final Objective sidebar;
    private final List<String> scoreList = Lists.newArrayList();

    public InfoSidebar(PlayerData data) {
        super(data);
        this.sidebar = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("sidebar", "dummy");
        this.sidebar.setDisplayName("Statistics");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        data.getPlayer().setScoreboard(sidebar.getScoreboard());
    }

    public void setData(List<String> data) {
        for (String score : scoreList) {
            sidebar.getScoreboard().resetScores(score);
        }
        scoreList.clear();
        scoreList.addAll(data);

        int counter = 1;
        for (String line : data) {
            sidebar.getScore(line).setScore(counter++);
        }
    }

}
