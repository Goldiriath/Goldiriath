package net.goldiriath.plugin.game.item.meta;

import lombok.Getter;
import org.bukkit.Material;

public enum ItemTier {

    BATTERED("Battered", 0.8, 0.7),
    NORMAL("Normal", 1, 1),
    CRAFTED("Crafted", 1.1, 1.3),
    RARE("Rare", 1.21, 1.6),
    LEGENDARY("Legendary", 1.33, 2.0);
    //
    private final String adjective;
    @Getter
    private final double weaponMulti;
    @Getter
    private final double armorMulti;

    private ItemTier(String adjective, double weaponMulti, double armorMulti) {
        this.adjective = adjective;
        this.weaponMulti = weaponMulti;
        this.armorMulti = armorMulti;
    }

    public String getAdjective() {
        return getAdjective(null);
    }

    public String getAdjective(Material material) {
        return adjective;
    }

    public static ItemTier fromName(String name) {
        name = name.toLowerCase();

        for (ItemTier tier : ItemTier.values()) {
            if (tier.toString().equalsIgnoreCase(name)
                    || tier.getAdjective().equalsIgnoreCase(name)) {
                return tier;
            }
        }

        return null;
    }
}
