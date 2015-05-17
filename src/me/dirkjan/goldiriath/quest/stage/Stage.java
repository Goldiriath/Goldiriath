package me.dirkjan.goldiriath.quest.stage;

import java.util.Collection;
import java.util.Map;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.quest.trigger.TriggerList;
import me.dirkjan.goldiriath.quest.trigger.Triggerable;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Registrable;
import org.bukkit.entity.Player;

public interface Stage extends ConfigLoadable, Triggerable<Player>, Registrable {

    public Quest getQuest();

    public String getId();

    public Map<String, TriggerList> getTriggerMap();

    public Collection<TriggerList> getTriggers();

}
