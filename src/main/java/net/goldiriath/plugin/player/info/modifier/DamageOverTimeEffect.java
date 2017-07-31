package net.goldiriath.plugin.player.info.modifier;

import net.goldiriath.plugin.Goldiriath;
import org.bukkit.entity.Player;

public class DamageOverTimeEffect extends OverTimeEffect {

    public DamageOverTimeEffect(int duration, int interval, int value) {
        super(duration, interval, value);
    }

    @Override
    protected void act(Player player) {
        Goldiriath.instance().hm.damage(player, value);
    }

}
