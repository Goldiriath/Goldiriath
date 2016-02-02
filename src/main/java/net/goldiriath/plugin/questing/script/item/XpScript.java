package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import org.bukkit.entity.Player;

public class XpScript extends ScriptItem {

    private final boolean add;
    private final int amt;

    public XpScript(Script script, String[] args) {
        super(script);

        add = args[1].equals("add");

        if (!add && !args[1].equals("remove")) {
            throw new ParseException("Unknown operation: " + args[1] + ". Needs to be either \"add\" or \"remove\".");
        }

        amt = parseInt(args[2]);
    }

    @Override
    public void execute(Player player) {
        if (add) {
            plugin.pm.getData(player).addXp(amt);
        } else {
            plugin.pm.getData(player).removeXp(amt);
        }
    }

}
