package net.goldiriath.plugin.questing.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.data.DataQuests;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.quest.Quest;
import net.goldiriath.plugin.questing.quest.triggering.TriggerList;
import net.goldiriath.plugin.questing.quest.triggering.TriggerParser;
import net.goldiriath.plugin.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.questing.quest.triggering.Triggerable;
import net.goldiriath.plugin.questing.script.ScriptContext;
import net.goldiriath.plugin.questing.script.ScriptParser;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Registrable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Stage implements ConfigLoadable, Triggerable<Player>, TriggerSource, Registrable {

    @Getter
    protected final Goldiriath plugin;
    @Getter
    protected final Quest quest;
    @Getter
    protected final String id;
    @Getter
    protected final Script script;
    protected final Map<String, TriggerList> triggers;

    public Stage(Quest quest, String id) {
        this.plugin = quest.getManager().getPlugin();
        this.quest = quest;
        this.id = id;
        this.script = new Script(plugin, new ScriptContext(quest));
        this.triggers = new HashMap<>();
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        // Load script
        script.clear();
        script.addAll(new ScriptParser(quest.getPlugin(), new ScriptContext(quest)).parse(config.getStringList("script")));

        // Load triggers
        triggers.clear();
        if (config.isConfigurationSection("triggers")) {

            for (String stageName : config.getConfigurationSection("triggers").getKeys(false)) {

                final Stage stage = quest.getStageMap().get(stageName);

                if (stage == null) {
                    plugin.logger.warning("[" + quest.getId() + "][" + id + "] Ignoring trigger: " + stageName + ". Unknown stage!");
                    continue;
                }

                final TriggerList stageTriggers = new TriggerParser(plugin, this).parse(config.getStringList("triggers." + id + ".actions"));
                stageTriggers.setTriggered(stage);

                triggers.put(stageName, stageTriggers);
                stageTriggers.register();
            }
        }
    }

    @Override
    public void register() {
        for (TriggerList stageTriggers : triggers.values()) {
            stageTriggers.register();
        }
    }

    @Override
    public void unregister() {
        for (TriggerList stageTriggers : triggers.values()) {
            stageTriggers.unregister();
        }
    }

    @Override
    public final void onTrigger(Player player) {
        final DataQuests data = plugin.pm.getData(player).getQuests();

        // Set current stage
        data.setStage(quest, this);

        // Execute script
        script.execute(player);
    }

    public Collection<TriggerList> getTriggers() {
        return triggers.values();
    }

    public Map<String, TriggerList> getTriggerMap() {
        return Collections.unmodifiableMap(triggers);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.quest);
        hash = 67 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final Stage other = (Stage) obj;

        return Objects.equals(this.quest, other.quest)
                && Objects.equals(this.id, other.id);
    }

}
