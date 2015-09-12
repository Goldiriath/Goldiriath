package net.goldiriath.plugin.quest.stage;

import java.util.Collection;
import java.util.Map;
import net.goldiriath.plugin.quest.Quest;
import net.goldiriath.plugin.quest.trigger.TriggerList;
import net.goldiriath.plugin.quest.trigger.Triggerable;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Registrable;
import org.bukkit.entity.Player;

public interface Stage extends ConfigLoadable, Triggerable<Player>, Registrable {

    public Quest getQuest();

    public String getId();

    public Map<String, TriggerList> getTriggerMap();

    public Collection<TriggerList> getTriggers();

}
