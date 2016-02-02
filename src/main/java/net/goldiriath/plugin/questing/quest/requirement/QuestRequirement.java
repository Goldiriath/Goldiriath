package net.goldiriath.plugin.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
import net.goldiriath.plugin.questing.quest.Quest;
import net.goldiriath.plugin.questing.quest.Stage;
import net.goldiriath.plugin.questing.script.ParseException;
import org.bukkit.entity.Player;

public class QuestRequirement extends AbstractRequirement {

    private final Quest quest;
    private final Stage stage;

    public QuestRequirement(Goldiriath plugin, String args[]) {
        super(plugin, Message.QUEST_QUEST_NOT_DONE);

        quest = plugin.qm.getQuest(args[1]);
        if (quest == null) {
            throw new ParseException("Quest '" + args[1] + "' not found.");
        }

        if (args[2].equals("started")) {
            stage = null;
        } else {
            stage = quest.getStageMap().get(args[2]);
            if (stage == null) {
                throw new ParseException("Stage '" + args[2] + "' not found.");
            }
        }

    }

    @Override
    public boolean has(Player player) {
        final Stage questStage = plugin.pm.getData(player).getQuests().getStage(quest);

        // Started?
        if (stage == null) {
            return questStage != null;
        }

        return questStage.equals(stage);
    }

}
