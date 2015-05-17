package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.Message;
import me.dirkjan.goldiriath.player.PlayerData;
import org.bukkit.entity.Player;

public class HealthRequirement extends AbstractRequirement {

    private final int health;
    private final boolean perc;

    public HealthRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_NOT_ENOUGH_HEALTH);

        if (args[1].contains("%")) {
            perc = true;
            args[1] = args[1].replace("%", "");
        } else {
            perc = false;
        }

        health = parseInt(args[1]);
    }

    @Override
    public boolean has(Player player) {
        PlayerData data = plugin.pm.getData(player);

        if (!perc) {
            return data.hasHealth(health);
        } else {
            return ((double) data.getHealth() / (double) data.getMaxHealth()) * 100 >= health;
        }
    }

}
