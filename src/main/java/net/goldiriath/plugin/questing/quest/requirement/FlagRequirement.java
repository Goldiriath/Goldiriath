package net.goldiriath.plugin.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.questing.script.ParseException;
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
        return op.operate(plugin.pm.getData(player).getFlags().get(flag), amount);
    }

}
