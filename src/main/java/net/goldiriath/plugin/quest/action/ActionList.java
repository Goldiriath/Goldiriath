package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.util.SafeArrayList;
import org.bukkit.entity.Player;

public class ActionList extends SafeArrayList<Action> implements Action {

    private static final long serialVersionUID = 57375756939123L;

    @Override
    public void execute(Player on) {
        for (Action a : this) {
            a.execute(on);
        }
    }
}
