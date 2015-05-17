package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.stage.Stage;
import org.bukkit.entity.Player;

public class ZapAction extends AbstractAction {

    final Stage stage;

    public ZapAction(Quest quest, String[] args) {
        super(quest);

        this.stage = quest.getStage(args[1]);

        if (stage == null) {
            throw new ParseException("Could not determine stage:" + stage);
        }
    }

    @Override
    public void execute(Player player) {
        stage.onTrigger(null, player); // TODO: is this right?
    }

}
