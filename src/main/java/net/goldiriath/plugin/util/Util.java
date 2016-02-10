package net.goldiriath.plugin.util;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
            return Goldiriath.instance().im.getItem(parseString.substring(1));
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

    public static String prepareLine(String line, Player player) {

        if (line.contains("<")) {
            PlayerData data = Goldiriath.instance().pm.getData(player);
            line = line
                    .replace("<player>", player.getName())
                    .replace("<money>", "" + data.getMoney())
                    .replace("<maxmoney>", "" + data.getMoney())
                    .replace("<mana>", "" + data.getMana())
                    .replace("<maxmana>", "" + data.getMaxMana())
                    .replace("<health>", "" + data.getHealth())
                    .replace("<mana>", "" + data.getMaxHealth());
        }

        return line;
    }

}
