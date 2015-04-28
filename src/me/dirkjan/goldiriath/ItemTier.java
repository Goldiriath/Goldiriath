package me.dirkjan.goldiriath;

import org.bukkit.Material;

public enum ItemTier {

    BAD("Battered"),
    NORMAL("Normal"),
    CRAFTED("Crafted"),
    GOOD("Rare"),
    AMAZING("Legendary");
    //
    private final String adjective;

    private ItemTier(String adjective) {
        this.adjective = adjective;
    }

    public String getAdjective(Material material) {
        return adjective;
    }
}
