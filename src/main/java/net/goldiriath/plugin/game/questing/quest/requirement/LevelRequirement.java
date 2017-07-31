package net.goldiriath.plugin.game.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.math.XPMath;
import org.bukkit.entity.Player;

public class LevelRequirement extends AbstractRequirement {

    private final int minimumLevel;

    public LevelRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_LEVEL_TO_LOW);
        minimumLevel = parseInt(args[1]) + 1;
    }

    @Override
    public boolean has(Player player) {
        return XPMath.xpToLevel(plugin.pm.getData(player).getXp()) >= minimumLevel;
    }

}
