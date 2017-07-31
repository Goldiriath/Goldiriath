package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.game.questing.dialog.OptionSet;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import net.goldiriath.plugin.game.questing.script.ScriptContext;
import org.bukkit.entity.Player;

public class OptionScript extends ScriptItem {

    private final OptionSet option;

    public OptionScript(Script script, String[] args) {
        super(script);
        context(ScriptContext.ScriptContextType.DIALOG);

        this.option = script.getContext().getDialog().getHandler().getOptionsMap().get(args[1]);

        if (option == null) {
            throw new ParseException("Could not find option: '" + args[1] + "'");
        }
    }

    @Override
    public void execute(Player player) {
        plugin.pm.getData(player).getDialogs().showOption(option);
    }

}
