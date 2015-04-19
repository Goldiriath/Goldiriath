package me.dirkjan.goldiriath;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {

    public static void msg(Player player, Profile profile, String message) {
        player.sendMessage(profile.getName() + ": " + message);
    }

    public static ItemStack parseItem(String parseString) {
        if (parseString == null) {
            return null;
        }

        final String[] parts = parseString.split(":");

        final Material type = Material.matchMaterial(parts[0]);
        if (type == null) {
            return null;
        }

        final byte data;
        try {
            if (parts.length == 1) {
                data = 0;
            } else if (parts.length == 2) {
                data = Byte.parseByte(parts[1]);
            } else {
                return null;
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        return new ItemStack(type, 1, (short) 0, data);
    }

}
