package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import org.bukkit.entity.Player;

public class MoneyAction extends AbstractAction {

    private final boolean add;
    private final int amt;

    public MoneyAction(Goldiriath plugin, String[] args) {
        super(plugin);

        add = args[1].equals("add");

        if (!add && !args[1].equals("sub")) {
            throw new ParseException("Unknown operation: " + args[1] + ". Needs to be either \"add\" or \"sub\"");
        }

        amt = parseInt(args[2]);
    }

    @Override
    public void execute(Player player) {
        if (add) {
            plugin.pm.getData(player).addMoney(amt);
        } else {
            plugin.pm.getData(player).removeMoney(amt);
        }
    }

}
