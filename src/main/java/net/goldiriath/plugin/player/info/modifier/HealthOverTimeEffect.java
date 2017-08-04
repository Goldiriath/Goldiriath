package net.goldiriath.plugin.player.info.modifier;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Util;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HealthOverTimeEffect extends OverTimeEffect {

    public HealthOverTimeEffect(int duration, int interval, int health) {
        super(duration, interval, health);
    }

    @Override
    public void act(Player player) {
        // Heal player
        Goldiriath.instance().dam.heal(player, value);
        Util.sound(player, Sound.BLOCK_NOTE_PLING, 0.9f);
        Util.effect(player, Effect.BOW_FIRE);
    }

}
