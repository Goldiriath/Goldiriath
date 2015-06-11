package me.dirkjan.goldiriath.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.player.QuestData;
import me.dirkjan.goldiriath.quest.requirement.RequirementList;
import me.dirkjan.goldiriath.quest.requirement.RequirementParser;
import me.dirkjan.goldiriath.quest.stage.QuestStage;
import me.dirkjan.goldiriath.quest.stage.Stage;
import me.dirkjan.goldiriath.quest.trigger.PlayerEventTrigger;
import me.dirkjan.goldiriath.quest.trigger.TriggerList;
import me.dirkjan.goldiriath.quest.trigger.TriggerParser;
import me.dirkjan.goldiriath.quest.trigger.Triggerable;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Quest implements ConfigLoadable, Validatable, Triggerable<Player> {

    public static final String STAGE_ENTRY_ID = "entry";
    public static final String STAGE_COMPLETE_ID = "complete";
    public static final String STAGE_CANCEL_ID = "cancel";
    //
    private final QuestManager manager;
    private final Goldiriath plugin;
    private final String id;
    //
    // Meta
    private String name;
    private List<String> description;
    private boolean ask; // TODO implement
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

    public QuestManager getManager() {
        return manager;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public Stage getStage(String id) {
        return stages.get(id);
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
        description = new ArrayList<>();
        description.addAll(Arrays.asList(desc));
        ask = config.getBoolean("meta.ask", true);

        // Requirements
        requirements.clear();
        requirements.addAll(new RequirementParser(plugin, id).parse(config.getStringList("requirements")));

        // Triggers
        triggers.clear();
        triggers.addAll(new TriggerParser(plugin, id).parse(config.getStringList("triggers")));

        if (!config.isConfigurationSection("stages")) {
            plugin.logger.warning("[" + id + "] Could not load quest. No stages defined!");
            return;
        }

        // Init and load stages
        for (String stageId : config.getConfigurationSection("stages").getKeys(false)) {
            if (!config.isConfigurationSection("stages." + stageId)) {
                plugin.logger.warning("[" + id + "] Skipping stage: " + stageId + ". Invalid format!");
                continue;
            }

            stages.put(stageId, new QuestStage(this, stageId));
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

        if (!stages.get(STAGE_COMPLETE_ID).getTriggers().isEmpty()) {
            throw new ParseException(String.format("'%s' stage may not contain triggers!", STAGE_COMPLETE_ID));
        }

        if (!stages.get(STAGE_CANCEL_ID).getTriggers().isEmpty()) {
            throw new ParseException(String.format("'%s' stage may not contain triggers!", STAGE_CANCEL_ID));
        }

        for (Stage stage : stages.values()) {
            try {
                stage.loadFrom(config.getConfigurationSection("stages." + stage.getId()));
            } catch (Exception ex) {
                final ParseException pex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex.getMessage(), ex));
                throw pex; // QuestManager will handle this
            }
        }

        // Start triggering quests
        triggers.setTriggered(this);
        triggers.register();
    }

    @Override
    public void onTrigger(PlayerEventTrigger trigger, Player player) {
        final QuestData data = plugin.pm.getData(player).getQuestData();

        // Check quest already started
        // Quests may be re-started if they're cancelled
        if (data.getStage(this) != null && data.getStage(this) != getCancelStage()) {
            return;
        }

        // Trigger entry stage
        getEntryStage().onTrigger(trigger, player); // TODO: Is this right?
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
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Quest && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
