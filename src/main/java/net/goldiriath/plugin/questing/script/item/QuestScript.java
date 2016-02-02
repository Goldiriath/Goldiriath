package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.questing.quest.Quest;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.quest.Stage;
import org.bukkit.entity.Player;

public class QuestScript extends ScriptItem {

    private final Stage stage;

    public QuestScript(Script script, String[] args) {
        super(script);

        Quest quest = plugin.qm.getQuest(args[1]);
        if (quest == null) {
            throw new ParseException("Could not find quest: " + args[1]);
        }

        switch (args[2]) {
            case "entry":
                stage = quest.getEntryStage();
                break;
            case "complete":
                stage = quest.getCompleteStage();
                break;
            case "cancel":
                stage = quest.getCancelStage();
                break;
            default:
                stage = quest.getStageMap().get(args[2]);
                if (stage == null) {
                    throw new ParseException("Unknown quest stage: " + args[2]);
                }
                break;
        }
    }

    @Override
    public void execute(Player player) {
        stage.onTrigger(player);
    }

}
