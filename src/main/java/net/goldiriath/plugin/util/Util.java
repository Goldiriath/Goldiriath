package net.goldiriath.plugin.util;

import net.goldiriath.plugin.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Util {

    private Util() {
    }

    @SuppressWarnings("deprecation")
    public static ItemStack parseItem(String parseString) {
        if (parseString == null) {
            return null;
        }

        if (parseString.startsWith("_")) {
            return Goldiriath.plugin.im.getItem(parseString.substring(1));
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

    public static void cancel(BukkitTask task) {
        try {
            task.cancel();
        } catch (Exception ex) {
        }
    }

    public static long getUnixSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

}
