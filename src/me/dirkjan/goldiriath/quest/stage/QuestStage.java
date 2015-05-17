package me.dirkjan.goldiriath.quest.stage;

import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.action.ActionParser;
import me.dirkjan.goldiriath.quest.trigger.TriggerList;
import me.dirkjan.goldiriath.quest.trigger.TriggerParser;
import org.bukkit.configuration.ConfigurationSection;

public class QuestStage extends AbstractStage {

    public QuestStage(Quest quest, String id) {
        super(quest, id);
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        // Unregister previous triggers
        unregister();

        // Load actions
        actions.clear();
        actions.addAll(new ActionParser(quest).parse(config.getStringList("actions")));

        // Load triggers
        triggers.clear();
        if (config.isConfigurationSection("triggers")) {

            for (String stageName : config.getConfigurationSection("triggers").getKeys(false)) {
                final Stage stage = quest.getStage(stageName);

                if (stage == null) {
                    plugin.logger.warning("[" + quest.getId() + "][" + id + "] Ignoring trigger: " + stageName + ". Unknown stage!");
                    continue;
                }

                final TriggerList stageTriggers = new TriggerParser(plugin, quest.getId()).parse(config.getStringList("triggers." + id + ".actions"));
                stageTriggers.setTriggered(stage);

                triggers.put(stageName, stageTriggers);
            }
        }

        // Register triggers
        register();
    }

}
