package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;
import org.bukkit.entity.Player;

public class FlagAction extends AbstractAction {

    private final boolean add;
    private final String flag;
    private final int amt;

    public FlagAction(Goldiriath plugin, String[] args) {
        super(plugin);
        add = args[1].equals("add");
        if (!args[1].equals("sub") && !add) {
            throw new ParseException("the first argument has to be eiter add or sub");
        }
        flag = args[2];
        amt = parseInt(args[3]);

    }

    @Override
    public void execute(Player player) {
        if (add) {
            plugin.pm.getData(player).addFlag(flag, amt);
        } else {
            plugin.pm.getData(player).removeFlag(flag, amt);

        }

    }

}
