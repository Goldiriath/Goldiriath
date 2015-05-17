package me.dirkjan.goldiriath.quest.trigger;

import me.dirkjan.goldiriath.util.Registrable;
import me.dirkjan.goldiriath.util.SafeArrayList;
import org.bukkit.entity.Player;

public class TriggerList extends SafeArrayList<PlayerEventTrigger> implements Registrable {

    public void trigger(Player player) {
        for (Trigger loopTrigger : this) {
            loopTrigger.trigger(player);
        }
    }

    public void setTriggered(Triggerable<Player> triggered) {
        for (Trigger trigger : this) {
            trigger.setTriggered(triggered);
        }
    }

    @Override
    public void register() {
        for (PlayerEventTrigger trigger : this) {
            trigger.register();
        }
    }

    @Override
    public void clear() {
        unregister();
        super.clear();
    }

    @Override
    public void unregister() {
        for (PlayerEventTrigger trigger : this) {
            trigger.unregister();
        }
    }

}
