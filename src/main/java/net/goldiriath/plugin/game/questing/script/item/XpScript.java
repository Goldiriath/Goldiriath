package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import org.bukkit.entity.Player;

public class XpScript extends ScriptItem {

    private final int amt;

    public XpScript(Script script, String[] args) {
        super(script);

        int tempAmt = parseInt(args[2]);

        if (args[1].equals("add")) {
            amt = tempAmt;
        } else if (args[1].equals("remove")) {
            amt = -tempAmt;
        } else {
            throw new ParseException("Unknown operation: " + args[1] + ". Needs to be either \"add\" or \"remove\".");
        }

    }

    @Override
    public void execute(Player player) {
        PlayerData data = plugin.pym.getData(player);

        data.setXp(data.getXp() + amt);
    }

}
