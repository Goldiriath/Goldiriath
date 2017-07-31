package net.goldiriath.plugin.game.questing.quest.triggering;

import net.goldiriath.plugin.game.questing.quest.triggering.trigger.AbstractTrigger;
import net.goldiriath.plugin.game.questing.quest.triggering.trigger.Trigger;
import net.goldiriath.plugin.util.Registrable;
import net.goldiriath.plugin.util.SafeArrayList;
import org.bukkit.entity.Player;

public class TriggerList extends SafeArrayList<AbstractTrigger> implements Registrable {

    private static final long serialVersionUID = 2346666126L;

    public void trigger(Player player) {
        for (Trigger<Player> loopTrigger : this) {
            loopTrigger.trigger(player);
        }
    }

    public void setTriggered(Triggerable<Player> triggered) {
        for (Trigger<Player> trigger : this) {
            trigger.setTriggered(triggered);
        }
    }

    @Override
    public void register() {
        for (AbstractTrigger trigger : this) {
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
        for (AbstractTrigger trigger : this) {
            trigger.unregister();
        }
    }

}
