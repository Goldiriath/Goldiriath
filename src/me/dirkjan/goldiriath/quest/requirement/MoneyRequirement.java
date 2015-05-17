package me.dirkjan.goldiriath.quest.requirement;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.Message;
import org.bukkit.entity.Player;

public class MoneyRequirement extends AbstractRequirement {

    private final int money;

    public MoneyRequirement(Goldiriath plugin, String[] args) {
        super(plugin, Message.QUEST_NOT_ENOUGH_MONEY);
        money = parseInt(args[1]);
    }

    @Override
    public boolean has(Player player) {
        return plugin.pm.getData(player).hasMoney(money);
    }

    @Override
    public void apply(Player player) {
        plugin.pm.getData(player).removeMoney(money);
    }

}
