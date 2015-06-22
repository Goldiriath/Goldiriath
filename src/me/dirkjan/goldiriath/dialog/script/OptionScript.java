package me.dirkjan.goldiriath.dialog.script;

import me.dirkjan.goldiriath.dialog.OptionSet;
import me.dirkjan.goldiriath.player.PlayerManager;
import me.dirkjan.goldiriath.quest.ParseException;
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
        pm.getPlayer(player).showOption(option);
    }

}
