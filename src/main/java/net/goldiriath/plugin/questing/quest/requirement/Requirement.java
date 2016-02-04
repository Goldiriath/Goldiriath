package net.goldiriath.plugin.questing.quest.requirement;

import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;

public interface Requirement {

    public boolean has(Player player);

    public BukkitMessage getMessage(Player player);

}
