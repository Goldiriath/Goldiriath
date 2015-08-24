package me.dirkjan.goldiriath.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.stage.Stage;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.ConfigSavable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class QuestData implements ConfigLoadable, ConfigSavable {

    @Getter
    private final PersistentData data;
    private final Goldiriath plugin;
    @Getter
    private final Player player;
    //
    private final Map<String, String> unparsed = new HashMap<>();
    private final Map<Quest, Stage> parsed = new HashMap<>();

    public QuestData(PersistentData data, Player player) {
        this.data = data;
        this.plugin = data.getData().getPlugin();
        this.player = player;
    }

    public void clear() {
        unparsed.clear();
        parsed.clear();
    }

    public void setStage(Quest quest, Stage stage) {
        unparsed.put(quest.getId(), stage.getId());
        parsed.put(quest, stage);
    }

    public Stage getStage(Quest quest) {
        return parsed.get(quest);
    }

    protected void parseData() {
        for (Entry<String, String> entry : unparsed.entrySet()) {
            final Quest quest = plugin.qm.getQuest(entry.getKey());
            if (quest == null) {
                plugin.logger.warning("Ignoring quest data for player: " + player + ", quest: " + entry.getKey() + ". Quest not found!");
                continue;
            }

            final Stage stage = quest.getStage(entry.getValue());
            if (stage == null) {
                plugin.logger.warning("Ignoring quest data for player: " + player + ", quest: " + entry.getKey() + ". Stage not found!");
                continue;
            }

            parsed.put(quest, stage);
        }
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        for (String questName : config.getKeys(false)) {
            final String stageName = config.getString(questName + ".stage");
            unparsed.put(stageName, questName);
        }

        parseData();
    }

    @Override
    public void saveTo(ConfigurationSection config) {
        for (Entry<String, String> entry : unparsed.entrySet()) {
            config.set(entry.getKey() + ".stage", entry.getValue());
        }
    }

}
