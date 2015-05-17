package me.dirkjan.goldiriath.quest.stage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.player.QuestData;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.action.ActionList;
import me.dirkjan.goldiriath.quest.trigger.PlayerEventTrigger;
import me.dirkjan.goldiriath.quest.trigger.TriggerList;
import org.bukkit.entity.Player;

public abstract class AbstractStage implements Stage {

    protected final Goldiriath plugin;
    protected final Quest quest;
    protected final String id;
    protected final ActionList actions;
    protected final Map<String, TriggerList> triggers;

    public AbstractStage(Quest quest, String id) {
        this.plugin = quest.getManager().getPlugin();
        this.quest = quest;
        this.id = id;
        this.actions = new ActionList();
        this.triggers = new HashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Quest getQuest() {
        return quest;
    }

    @Override
    public Collection<TriggerList> getTriggers() {
        return triggers.values();
    }

    @Override
    public Map<String, TriggerList> getTriggerMap() {
        return triggers;
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
    public void onTrigger(PlayerEventTrigger trigger, Player player) {
        final QuestData data = plugin.pm.getData(player).getQuestData();

        if (trigger == null) {
            plugin.logger.warning("[" + quest.getId() + "][" + id + "] Stage triggered without proper trigger!");
        }

        // Validate previous stage:
        // Triggers may only be processed if they are triggered by the currently active stage
        final Stage prevStage = data.getStage(quest);
        if (prevStage != null && trigger != null) {
            final TriggerList prevTriggers = prevStage.getTriggerMap().get(id);

            if (prevTriggers == null || !prevTriggers.contains(trigger)) {
                return;
            }
        }

        // Set current stage
        data.setStage(quest, this);

        // Execute actions
        actions.execute(player);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Quest && obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.quest);
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

}
