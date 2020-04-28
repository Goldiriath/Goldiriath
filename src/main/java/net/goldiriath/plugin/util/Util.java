package net.goldiriath.plugin.util;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

public class Util {

    private Util() {
    }

    public static ItemStack parseItem(String parseString) {
        if (parseString == null) {
            return null;
        }

        if (parseString.startsWith("_")) {
            return Goldiriath.instance().itm.getItem(parseString.substring(1));
        }

        int modelData = 0;
        if (parseString.contains(":")) {
            String[] split = parseString.split(":");
            parseString = split[0];
            try {
                Integer.parseInt(split[1]);
            } catch (Exception ex) {
                return null;
            }
        }

        final Material type = parseMaterial(parseString);
        if (type == null) {
            return null;
        }

        // Set model data
        ItemStack s = new ItemStack(type, 1);
        if (modelData != 0) {
            ItemMeta m = s.getItemMeta();
            if (m == null) {
                return null;
            }
            m.setCustomModelData(modelData);
            s.setItemMeta(m);
        }

        return s;
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
            PlayerData data = Goldiriath.instance().pym.getData(player);
            line = line
                    .replace("<player>", player.getName())
                    .replace("<money>", "" + data.getMoney())
                    .replace("<maxmoney>", "" + data.getMoney())
                    .replace("<mana>", "" + data.getMana())
                    .replace("<maxmana>", "" + data.getMaxMana())
                    .replace("<health>", "" + data.getHealth())
                    .replace("<mana>", "" + data.getMaxHealth());
        }

        return ChatColor.translateAlternateColorCodes('&', line);
    }

    public static void sound(Player player, Sound sound, float pitch) {
        player.playSound(player.getLocation(), sound, 1.0f, pitch);
    }

    public static void effect(Player player, Effect effect) {
        player.playEffect(player.getLocation(), effect, null);
    }

    public static Player nearby(Projectile p, ProjectileSource ignored) {
        for (Entity e : p.getNearbyEntities(1, 1, 1)) {
            if (!(e instanceof Player)) {
                continue;
            }

            if (ignored != null && p.getShooter().equals(ignored)) {
                continue;
            }

            return (Player) e;
        }

        return null;
    }

}
