package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.entity.Player;

public class SkillpointAction extends AbstractAction {

    private final int amt;

    public SkillpointAction(Goldiriath plugin, String[] args) {
        super(plugin);
        amt = parseInt(args[1]);
    }

    @Override
    public void execute(Player player) {
        plugin.pm.getData(player).addSkillPoints(amt);
    }

}
