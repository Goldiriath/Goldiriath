package net.goldiriath.plugin.game.questing.script.item;

import net.goldiriath.plugin.game.questing.quest.Quest;
import net.goldiriath.plugin.game.questing.quest.Stage;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.game.questing.script.Script;
import org.bukkit.entity.Player;

public class QuestScript extends ScriptItem {

    private final Quest quest;
    private final String stageId;

    public QuestScript(Script script, String[] args) {
        super(script);

        quest = plugin.qm.getQuest(args[1]);
        if (quest == null) {
            throw new ParseException("Could not find quest: " + args[1]);
        }

        // Quests may be lazily loaded due to quest/dialog cyclic dependency
        stageId = args[2];
    }

    @Override
    public void execute(Player player) {
        Stage stage;
        switch (stageId) {
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
                stage = quest.getStageMap().get(stageId);
                if (stage == null) {
                    throw new ParseException("Unknown quest stage for quest '" + quest.getId() + "': " + stageId);
                }
                break;
        }

        stage.onTrigger(player);
    }

}
