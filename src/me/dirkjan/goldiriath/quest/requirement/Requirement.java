package me.dirkjan.goldiriath.quest.requirement;

import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;

public interface Requirement {

    public boolean has(Player player);

    public void apply(Player player);

    public BukkitMessage getMessage(Player player);

}
