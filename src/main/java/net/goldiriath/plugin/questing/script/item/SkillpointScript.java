package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.questing.script.Script;
import org.bukkit.entity.Player;

public class SkillpointScript extends ScriptItem {

    private final int amt;

    public SkillpointScript(Script script, String[] args) {
        super(script);
        amt = parseInt(args[1]);
    }

    @Override
    public void execute(Player player) {
        PlayerData data = plugin.pm.getData(player);
        data.setSkillPoints(data.getSkillPoints() + amt);
    }

}
