package net.goldiriath.plugin.player.info.modifier;

import net.goldiriath.plugin.util.Util;
import org.bukkit.entity.Player;

public abstract class Effect {

    protected final long start;
    protected final int duration;

    public Effect(int duration) {
        this.start = Util.getServerTick();
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
    
    public boolean isOver() {
        return Util.getServerTick() > start + duration;
    }
    
    public abstract void tick(Player player);
}
