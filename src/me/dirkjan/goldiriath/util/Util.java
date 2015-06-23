package me.dirkjan.goldiriath.util;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ServerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {

    private Util() {
    }

    public static void msg(Player player, ServerProfile profile, String message) {
        player.sendMessage(profile.getName() + ": " + message);
    }

    public static ItemStack parseItem(String parseString) {
        if (parseString == null) {
            return null;
        }

        if (parseString.startsWith("_")) {
            return Goldiriath.plugin.is.getItem(parseString.substring(1));
        }

        final String[] parts = parseString.split(":");

        final Material type = parseMaterial(parts[0]);
        if (type == null) {
            return null;
        }

        final byte data;
        try {
            if (parts.length == 2) {
                data = new Integer(Integer.parseInt(parts[1])).byteValue();
            } else {
                data = 0;
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        return new ItemStack(type, 1, (short) 0, data);
    }

    public static Material parseMaterial(String material) {
        Material type = Material.matchMaterial(material);
        if (type == null) {
            type = Material.getMaterial(material);
        }
        return type;
    }

    public static long getServerTick() {
        return Bukkit.getWorlds().get(0).getFullTime();
    }

}
