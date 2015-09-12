package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.Goldiriath;
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
