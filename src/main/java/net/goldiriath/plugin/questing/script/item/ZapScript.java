package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.quest.triggering.Triggerable;
import org.bukkit.entity.Player;

public class ZapScript extends ScriptItem {

    private final Triggerable<Player> triggered;

    public ZapScript(Script script, String[] args) {
        super(script);

        String id = args[1].toLowerCase();
        switch (context().getType()) {
            case DIALOG:
                triggered = context().getDialog().getHandler().getDialogsMap().get(id);
                if (triggered == null) {
                    throw new ParseException("Could not find dialog: " + id);
                }
                break;
            case QUEST:
                triggered = context().getQuest().getStageMap().get(id);
                if (triggered == null) {
                    throw new ParseException("Could not find stage: " + id);
                }
                break;
            default:
                invalidContext();
                triggered = null;
                break;
        }
    }

    @Override
    public void execute(Player player) {

        // Currently: Check requirements before zapping
        triggered.onTrigger(player);

        // Possibility: Ignore dialog requirements
        //script.getDialog().getHandler().getManager().getPlugin().pm.getData(player).startDialog(newDialog);
    }

}
