package net.goldiriath.plugin.game.item.meta;

import lombok.Getter;

public enum ArmorType {
    HEAVY(1.5),
    LIGHT(1);
    //
    @Getter
    private final double multiplier;

    private ArmorType(double m) {
        this.multiplier = m;
    }

}
