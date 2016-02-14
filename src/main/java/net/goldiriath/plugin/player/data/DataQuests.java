package net.goldiriath.plugin.player.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.questing.quest.Quest;
import net.goldiriath.plugin.questing.quest.Stage;
import org.bukkit.configuration.ConfigurationSection;

public class DataQuests extends Data {

    private final Map<String, String> unparsed = new HashMap<>(); // quest-id -> stage-id
    private final Map<Quest, Stage> parsed = new HashMap<>();

    public DataQuests(PlayerData data) {
        super(data, "quests");
    }

    public void clear() {
        unparsed.clear();
        parsed.clear();
    }

    public void setStage(Quest quest, Stage stage) {
        unparsed.put(quest.getId(), stage.getId());
        parsed.put(quest, stage);
    }

    public void reset(Quest quest) {
        unparsed.remove(quest.getId());
        parsed.remove(quest);
    }

    public Stage getStage(Quest quest) {
        return parsed.get(quest);
    }

    protected void parseData() {
        for (Entry<String, String> entry : unparsed.entrySet()) {
            final Quest quest = plugin.qm.getQuest(entry.getKey());
            if (quest == null) {
                plugin.logger.warning("Ignoring quest data for player: " + data.getPlayer().getName() + ", quest: " + entry.getKey() + ". Quest not found!");
                continue;
            }

            final Stage stage = quest.getStageMap().get(entry.getValue());
            if (stage == null) {
                plugin.logger.warning("Ignoring quest data for player: " + data.getPlayer().getName() + ", quest: " + entry.getKey() + ". Stage not found!");
                continue;
            }

            parsed.put(quest, stage);
        }
    }

    @Override
    protected void load(ConfigurationSection config) {
        for (String questName : config.getKeys(false)) {
            final String stageName = config.getString(questName + ".stage");
            unparsed.put(questName, stageName);
        }

        parseData();
    }

    @Override
    protected void save(ConfigurationSection config) {
        for (Entry<String, String> entry : unparsed.entrySet()) {
            config.set(entry.getKey() + ".stage", entry.getValue());
        }
    }

}
