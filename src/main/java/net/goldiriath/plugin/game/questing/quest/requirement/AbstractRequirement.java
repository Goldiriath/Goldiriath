package net.goldiriath.plugin.game.questing.quest.requirement;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.pravian.aero.base.PluginMessage;
import org.bukkit.entity.Player;

public abstract class AbstractRequirement implements Requirement {

    protected final Goldiriath plugin;
    final PluginMessage message;

    public AbstractRequirement(Goldiriath plugin, PluginMessage message) {
        this.plugin = plugin;
        this.message = message;
    }

    @Override
    public PluginMessage getMessage(Player player) {
        return message;
    }

    // Util methods
    public final int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid number: " + string + "!");
        }
    }

}
