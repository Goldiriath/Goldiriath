package net.goldiriath.plugin.item;

import org.bukkit.Material;

public enum ItemTier {

    BATTERED("Battered"),
    NORMAL("Normal"),
    CRAFTED("Crafted"),
    RARE("Rare"),
    AMAZING("Legendary");
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
            if (tier.toString().toLowerCase().equals(name)
                    || tier.getAdjective().toLowerCase().equals(name)) {
                return tier;
            }
        }

        return null;
    }
}
