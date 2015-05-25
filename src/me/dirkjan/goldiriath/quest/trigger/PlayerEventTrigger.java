package me.dirkjan.goldiriath.quest.trigger;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.listener.RegistrableListener;
import me.dirkjan.goldiriath.quest.ParseException;
import org.bukkit.entity.Player;

public class PlayerEventTrigger extends RegistrableListener implements Trigger<Player> {

    protected Triggerable<Player> triggered;

    public PlayerEventTrigger(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    public void setTriggered(Triggerable<Player> triggered) {
        this.triggered = triggered;
    }

    @Override
    public Triggerable<Player> getTriggered() {
        return triggered;
    }

    @Override
    public void trigger(Player triggerer) {
        if (triggered != null) {
            triggered.onTrigger(this, triggerer);
        }
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