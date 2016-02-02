package net.goldiriath.plugin.questing.quest.triggering.trigger;

import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.quest.Stage;
import net.goldiriath.plugin.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.questing.quest.triggering.Triggerable;
import net.goldiriath.plugin.util.RegistrableListener;
import org.bukkit.entity.Player;

public abstract class AbstractTrigger extends RegistrableListener implements Trigger<Player> {

    @Getter
    protected final TriggerSource source;
    @Getter
    @Setter
    protected Triggerable<Player> triggered;

    public AbstractTrigger(TriggerSource source) {
        super(source.getPlugin());
        this.source = source;
    }

    @Override
    public void trigger(Player triggerer) {
        if (triggered == null) {
            return;
        }

        // Only let stages triggers trigger if they're currently in that stage
        if (source instanceof Stage) {
            final Stage currentStage = plugin.pm.getData(triggerer).getQuests().getStage(((Stage) source).getQuest());
            if (!currentStage.equals(source)) {
                return;
            }
        }

        triggered.onTrigger(triggerer);
    }

    // Util methods
    public int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid number: " + string + "!");
        }
    }

}
