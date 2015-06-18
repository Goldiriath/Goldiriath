package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.trigger.Triggerable;
import org.bukkit.entity.Player;

public class ZapAction extends AbstractAction {

    private final Triggerable<Player> triggered;

    public ZapAction(Quest quest, String[] args) {
        super(quest.getManager().getPlugin());

        this.triggered = quest.getStage(args[1]);

        if (triggered == null) {
            throw new ParseException("Could not determine stage:" + triggered);
        }
    }

    public ZapAction(Goldiriath plugin, Triggerable<Player> triggered) {
        super(plugin);

        this.triggered = triggered;
    }

    @Override
    public void execute(Player player) {
        triggered.onTrigger(null, player); // TODO: is this right?
    }

}
