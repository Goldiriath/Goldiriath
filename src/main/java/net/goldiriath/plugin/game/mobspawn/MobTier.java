package net.goldiriath.plugin.game.mobspawn;

import lombok.Getter;
import net.goldiriath.plugin.game.loot.TierContainer;

public enum MobTier implements TierContainer {

    SWARM(10),
    NORMAL(50),
    ELITE(75),
    BOSS(100);
    //
    @Getter
    private final int tierValue;

    private MobTier(int index) {
        this.tierValue = index;
    }

}
