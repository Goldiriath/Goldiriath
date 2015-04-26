package me.dirkjan.goldiriath;

import me.dirkjan.goldiriath.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public enum Profile {

    TORE("Tore"),
    ALICE("Alice"),
    CAPTAIN("Captain");
    //
    private final String name;

    private Profile(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void msg(Player player, String message) {
        Util.msg(player, this, message);
    }

    public void msg(int delay, final Player player, final String message) {

        final Profile msgprofile = this;

        new BukkitRunnable() {

            @Override
            public void run() {
                Util.msg(player, msgprofile, message);
            }
        }.runTaskLater(Goldiriath.plugin, delay);
    }
}
