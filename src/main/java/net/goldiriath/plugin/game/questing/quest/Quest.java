package net.goldiriath.plugin.game.questing.quest;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.data.DataQuests;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementList;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementParser;
import net.goldiriath.plugin.game.questing.quest.triggering.TriggerList;
import net.goldiriath.plugin.game.questing.quest.triggering.TriggerParser;
import net.goldiriath.plugin.game.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.game.questing.quest.triggering.Triggerable;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Quest implements ConfigLoadable, Triggerable<Player>, TriggerSource, Validatable {

    public static final String STAGE_ENTRY_ID = "entry";
    public static final String STAGE_COMPLETE_ID = "complete";
    public static final String STAGE_CANCEL_ID = "cancel";
    //
    @Getter
    private final QuestManager manager;
    @Getter
    private final Goldiriath plugin;
    @Getter
    private final String id;
    //
    // Meta
    private String name;
    private List<String> description;
    //
    // Entries
    private final RequirementList requirements;
    private final TriggerList triggers;
    private final Map<String, Stage> stages;

    public Quest(QuestManager manager, String id) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.id = id;
        //
        this.requirements = new RequirementList();
        this.triggers = new TriggerList();
        this.stages = new HashMap<>();
    }

    public Collection<Stage> getStages() {
        return stages.values();
    }

    public Map<String, Stage> getStageMap() {
        return Collections.unmodifiableMap(stages);
    }

    public Stage getEntryStage() {
        return stages.get(STAGE_ENTRY_ID);
    }

    public Stage getCompleteStage() {
        return stages.get(STAGE_COMPLETE_ID);
    }

    public Stage getCancelStage() {
        return stages.get(STAGE_CANCEL_ID);
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        // Unregister previous triggers
        triggers.unregister();

        // Clear previous entries
        requirements.clear();
        triggers.clear();

        // Get meta
        name = config.getString("meta.name", null);
        String[] desc = config.getString("meta.description", "").split("\\n");
        description = Lists.newArrayList();
        description.addAll(Arrays.asList(desc));

        // Requirements
        requirements.clear();
        requirements.addAll(new RequirementParser(plugin, id).parse(config.getStringList("requirements")));

        // Triggers
        triggers.clear();
        triggers.addAll(new TriggerParser(plugin, this).parse(config.getStringList("triggers")));

        if (!config.isConfigurationSection("stages")) {
            plugin.logger.warning("[" + id + "] Could not load quest. No stages defined!");
            return;
        }

        // Preload stages
        stages.clear();
        for (String stageId : config.getConfigurationSection("stages").getKeys(false)) {
            if (!config.isConfigurationSection("stages." + stageId)) {
                plugin.logger.warning("[" + id + "] Skipping stage: " + stageId + ". Invalid format!");
                continue;
            }

            stages.put(stageId, new Stage(this, stageId));
        }

        if (!stages.containsKey(STAGE_ENTRY_ID)
                || !stages.containsKey(STAGE_COMPLETE_ID)
                || !stages.containsKey(STAGE_CANCEL_ID)) {
            throw new ParseException(String.format(
                    "Quest does not contain all required stages: '%s', '%s', '%s'!",
                    STAGE_ENTRY_ID,
                    STAGE_COMPLETE_ID,
                    STAGE_CANCEL_ID));
        }

        // Init stages
        for (Stage stage : stages.values()) {
            try {
                stage.loadFrom(config.getConfigurationSection("stages." + stage.getId()));
            } catch (Exception ex) {
                final ParseException pex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex));
                throw pex; // QuestManager will handle this
            }
        }

        if (!stages.get(STAGE_COMPLETE_ID).getTriggers().isEmpty()) {
            throw new ParseException(String.format("'%s' stage may not contain triggers!", STAGE_COMPLETE_ID));
        }

        if (!stages.get(STAGE_CANCEL_ID).getTriggers().isEmpty()) {
            throw new ParseException(String.format("'%s' stage may not contain triggers!", STAGE_CANCEL_ID));
        }

        // Start triggering this quest
        triggers.setTriggered(this);
        triggers.register();
    }

    @Override
    public void onTrigger(Player player) {
        final DataQuests data = plugin.pym.getData(player).getQuests();

        // Check quest already started
        // Quests may be re-started if they're cancelled
        Stage current = data.getStage(this);
        if (current != null && current != getCancelStage()) {
            return;
        }

        // Trigger entry stage
        getEntryStage().onTrigger(player);
    }

    @Override
    public boolean isValid() {
        return name != null
                && !name.isEmpty()
                && !description.isEmpty()
                && !description.get(0).isEmpty()
                && !stages.isEmpty()
                && stages.containsKey(STAGE_ENTRY_ID)
                && stages.containsKey(STAGE_COMPLETE_ID)
                && stages.containsKey(STAGE_CANCEL_ID);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Quest other = (Quest) obj;

        return Objects.equals(this.id, other.id);
    }

}
