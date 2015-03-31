package me.dirkjan.goldiriath;

import org.bukkit.entity.Player;

public class Util {

    public static void msg(Player player, Profile profile, String message) {
        player.sendMessage(profile.getName() + ": " + message);
    }

}
