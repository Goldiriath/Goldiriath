package net.goldiriath.plugin.loot;

import lombok.Getter;

public enum ChestTier implements TierContainer {

    SWARM(10),
    NORMAL(50),
    ELITE(75),
    BOSS(100);
    //
    @Getter
    private final int tierValue;

    private ChestTier(int index) {
        this.tierValue = index;
    }
}
