package net.goldiriath.plugin.player.info.modifier;

import net.goldiriath.plugin.util.Util;
import org.bukkit.entity.Player;

public abstract class OverTimeEffect extends Effect {

    protected final int interval;
    protected final int value;

    public OverTimeEffect(int duration, int interval, int value) {
        super(duration);
        this.interval = interval;
        this.value = value;
    }

    public int getInterval() {
        return interval;
    }

    public int getValue() {
        return value;
    }

    @Override
    public final void tick(Player player) {
        // The interval is 1 here because modifiers only start ticking
        // 1 tick after it is added.
        if ((Util.getServerTick() - start) % interval == 1) {
            act(player);
        }
    }

    protected abstract void act(Player player);

}
