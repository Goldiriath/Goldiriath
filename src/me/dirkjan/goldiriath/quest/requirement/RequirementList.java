package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Message;
import me.dirkjan.goldiriath.util.SafeArrayList;
import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;

public class RequirementList extends SafeArrayList<Requirement> implements Requirement {

    private static final long serialVersionUID = 473717123927L;

    public Requirement getFailingRequirement(Player player) {
        for (Requirement r : this) {
            if (r.has(player)) {
                continue;
            }

            // Hackity hack 8D
            while (r instanceof RequirementList) {
                r = ((RequirementList) r).getFailingRequirement(player);
            }

            return r;
        }

        return null;
    }

    @Override
    public boolean has(Player player) {
        for (Requirement r : this) {
            if (!r.has(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void apply(Player player) {
        for (Requirement r : this) {
            r.apply(player);
        }
    }

    @Override
    public BukkitMessage getMessage(Player player) {
        Requirement req = getFailingRequirement(player);
        return req != null ? req.getMessage(player) : Message.QUEST_GENERIC_REQUIREMENT;
    }

}
