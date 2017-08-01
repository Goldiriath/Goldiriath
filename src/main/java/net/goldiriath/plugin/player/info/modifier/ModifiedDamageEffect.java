package net.goldiriath.plugin.player.info.modifier;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Created by koen on 01/08/2017.
 */
public class ModifiedDamageEffect extends Effect {
    @Getter
    private double reduce;

    public ModifiedDamageEffect(int duration, double reduceBy) {
        super(duration);
        this.reduce = reduceBy;
    }

    @Override
    public void tick(Player player) {

    }
}
