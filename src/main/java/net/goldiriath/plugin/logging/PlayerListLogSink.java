package net.goldiriath.plugin.logging;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Level;
import net.goldiriath.plugin.util.PlayerList;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerListLogSink implements LogSink {

    private final PlayerList list;

    public PlayerListLogSink() {
        this(new PlayerList());
    }

    public PlayerListLogSink(PlayerList list) {
        this.list = list;
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        final List<String> messages = Lists.newArrayList();

        String prefix = ChatColor.GRAY + "[" + ChatColor.RED + "GLOG" + ChatColor.GRAY + "]";
        if (level != null) {
            prefix += ChatColor.GOLD + "[" + ChatColor.WHITE + level.getName().toUpperCase() + ChatColor.GOLD + "]";
        }

        // Message color
        prefix+= ChatColor.WHITE;

        if (!prefix.endsWith(" ")) {
            prefix += " ";
        }

        if (message != null) {
            messages.add(prefix + message);
        }

        if (t != null) {
            for (String s : ExceptionUtils.getFullStackTrace(t).split("\n")) {
                messages.add(prefix + s);
            }
        }

        for (Player player : list.getPlayerMembers()) {
            try {
                for (String playerMsg : messages) {
                    player.sendMessage(playerMsg);
                }
            } catch (Exception ex) {
            }
        }
    }

}
