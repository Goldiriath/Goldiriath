package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.Message;
import me.dirkjan.goldiriath.player.PlayerData;
import org.bukkit.entity.Player;

public class ManaRequirement extends AbstractRequirement {

    private final int mana;
    private final boolean perc;

    public ManaRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_NOT_ENOUGH_MANA);

        if (args[1].contains("%")) {
            perc = true;
            args[1] = args[1].replace("%", "");
        } else {
            perc = false;
        }

        mana = parseInt(args[1]);
    }

    @Override
    public boolean has(Player player) {
        final PlayerData data = plugin.pm.getData(player);

        if (!perc) {
            return data.hasMana(mana);
        } else {
            return ((double) data.getMana() / (double) data.getMana()) * 100 >= mana;
        }
    }

}
