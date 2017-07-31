package net.goldiriath.plugin.game.questing.quest.requirement;

import net.pravian.aero.base.PluginMessage;
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
    public PluginMessage getMessage(Player player) {
        return requirement.getMessage(player); // TODO: How do we handle this more properly?
    }

}
