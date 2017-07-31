package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import org.bukkit.entity.Player;

public class FlagScript extends ScriptItem {

    private final String flag;
    private final boolean set;
    private final int amt;

    public FlagScript(Script script, String[] args) {
        super(script);

        flag = args[1].toLowerCase();

        switch (args[2]) {
            case "add":
                set = false;
                amt = parseInt(args[3]);
                break;
            case "remove":
                set = false;
                amt = -parseInt(args[3]);
                break;
            case "reset":
                set = true;
                amt = 0;
                break;
            case "set":
                set = true;
                amt = parseInt(args[3]);
                break;
            default:
                throw new ParseException("Could not find find operation: '" + args[2] + "'");
        }

    }

    @Override
    public void execute(Player player) {
        if (set) {
            plugin.pm.getData(player).getFlags().put(flag, amt);
        } else {
            plugin.pm.getData(player).getFlags().increment(flag, amt);
        }
    }

}
