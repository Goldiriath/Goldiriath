package me.dirkjan.goldiriath.quest;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public enum ServerProfile { // TODO: Remove, redesign or something

    TORE("Tore"),
    ALICE("Alice"),
    CAPTAIN("Captain");
    //
    private final String name;

    private ServerProfile(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void msg(Player player, String message) {
        Util.msg(player, this, message);
    }

    public void msg(int delay, final Player player, final String message) {

        final ServerProfile msgprofile = this;

        new BukkitRunnable() {

            @Override
            public void run() {
                Util.msg(player, msgprofile, message);
            }
        }.runTaskLater(Goldiriath.plugin, delay);
    }
}
