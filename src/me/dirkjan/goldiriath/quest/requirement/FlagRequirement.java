package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.Message;
import me.dirkjan.goldiriath.quest.ParseException;
import org.bukkit.entity.Player;

public class FlagRequirement extends AbstractRequirement {

    private final String flag;
    private final Operatable op;
    private final int amount;

    public FlagRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.NO_MESSAGE);

        this.flag = args[1].toLowerCase();
        this.op = Operator.fromOperator(args[2]);
        this.amount = parseInt(args[3]);

        if (op == null) {
            throw new ParseException("Unknown operator: '" + args[2] + "'");
        }
    }

    @Override
    public boolean has(Player player) {
        return op.operate(plugin.pm.getData(player).getFlag(flag), amount);
    }

}
