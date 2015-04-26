package net.goldiriath.plugin.game.damage.modifier;

import lombok.Data;
import lombok.Getter;

public class Modifier {

    @Getter
    private final ModifierType type;
    @Getter
    private final double value;
    @Getter
    private final int duration;
    
    public Modifier(ModifierType type, double value) {
        this(type, value, 0);
    }
    
    public Modifier(ModifierType type, double value, int duration) {
        this.type = type;
        this.value = value;
        this.duration = duration;
    }
    
}
