package net.goldiriath.plugin.game.questing.quest.requirement;

import net.pravian.aero.base.PluginMessage;
import org.bukkit.entity.Player;

public interface Requirement {

    public boolean has(Player player);

    public PluginMessage getMessage(Player player);

}
