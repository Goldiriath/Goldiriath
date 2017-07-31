package net.goldiriath.plugin.game.item.meta;

import org.bukkit.Material;

public enum ItemTier {

    BATTERED("Battered"),
    NORMAL("Normal"),
    CRAFTED("Crafted"),
    RARE("Rare"),
    LEGENDARY("Legendary");
    //
    private final String adjective;

    private ItemTier(String adjective) {
        this.adjective = adjective;
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
