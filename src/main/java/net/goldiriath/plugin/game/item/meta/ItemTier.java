package net.goldiriath.plugin.game.item.meta;

import lombok.Getter;
import org.bukkit.Material;

public enum ItemTier {

    BATTERED("Battered", 0.8, 0.7),
    NORMAL("Normal", 1.0, 1.0),
    CRAFTED("Crafted", 1.1, 1.3),
    RARE("Rare", 1.21, 1.6),
    LEGENDARY("Legendary", 1.33, 2.0);
    //
    private final String adjective;
    @Getter
    private final double weaponMultiplier;
    @Getter
    private final double armorMultiplier;

    private ItemTier(String adjective, double weaponMulti, double armorMulti) {
        this.adjective = adjective;
        this.weaponMultiplier = weaponMulti;
        this.armorMultiplier = armorMulti;
    }

    public String getAdjective() {
        return getAdjective(null);
    }

    public String getAdjective(Material material) {
        return adjective;
    }
}
