package net.goldiriath.plugin.dialog.script;

import net.goldiriath.plugin.dialog.OptionSet;
import net.goldiriath.plugin.player.PlayerManager;
import net.goldiriath.plugin.quest.ParseException;
import org.bukkit.entity.Player;

public class OptionScript extends ScriptItem {

    private final PlayerManager pm;
    private final OptionSet option;

    public OptionScript(Script script, String[] args) {
        super(script);

        this.pm = script.getDialog().getHandler().getManager().getPlugin().pm;

        this.option = script.getDialog().getHandler().getOptionsMap().get(args[1]);

        if (option == null) {
            throw new ParseException("Could not find option: '" + args[1] + "'");
        }
    }

    @Override
    public void execute(Player player) {
        pm.getData(player).showOption(option);
    }

}
