package me.dirkjan.goldiriath.quest.requirement;

import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;

public class InvertedRequirement implements Requirement {

    private final Requirement requirement;

    public InvertedRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean has(Player player) {
        return !requirement.has(player);
    }

    @Override
    public void apply(Player player) {
        requirement.apply(player);
    }

    @Override
    public BukkitMessage getMessage(Player player) {
        return requirement.getMessage(player); // TODO: How do we handle this more properly?
    }

}
