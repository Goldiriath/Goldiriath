package net.goldiriath.plugin.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.Message;
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
