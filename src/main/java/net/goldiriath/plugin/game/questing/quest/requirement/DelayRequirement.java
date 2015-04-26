package net.goldiriath.plugin.game.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.player.data.DataFlags;
import net.goldiriath.plugin.util.Util;
import org.bukkit.entity.Player;

public class DelayRequirement extends AbstractRequirement {

    // This offset is deducted from the unix timestamp so that we
    // can store it as an int for quite some time.
    public static int TIME_OFFSET = 1486468286;

    private final String flag;
    private final int delaySeconds;

    public DelayRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.NO_MESSAGE);

        flag = "delay-" + args[1];
        delaySeconds = parseInt(args[2]);
    }

    @Override
    public boolean has(Player player) {

        final DataFlags flags = plugin.pm.getData(player).getFlags();

        if (!flags.has(flag)) {
            flags.put(flag, getOffsetTime());
            return true;
        }

        final int currentTime = getOffsetTime();
        final int flagTime = flags.get(flag);

        if (currentTime - flagTime < delaySeconds) {
            return false;
        }

        flags.put(flag, currentTime);
        return true;
    }

    private int getOffsetTime() {
        return (int) (Util.getUnixSeconds() - TIME_OFFSET);
    }

}
