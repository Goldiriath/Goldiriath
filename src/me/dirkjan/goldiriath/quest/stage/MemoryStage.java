package me.dirkjan.goldiriath.quest.stage;

import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.action.ActionList;
import org.bukkit.configuration.ConfigurationSection;

public class MemoryStage extends AbstractStage {

    public MemoryStage(Quest quest, String id, ActionList actions) {
        super(quest, id);
        this.actions.addAll(actions);
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
    }

    @Override
    public void register() {
    }

    @Override
    public void unregister() {

    }

}
