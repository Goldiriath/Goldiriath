package net.goldiriath.plugin.quest.requirement;

import net.goldiriath.plugin.quest.ParseException;
import net.goldiriath.plugin.Goldiriath;
import net.pravian.bukkitlib.command.BukkitMessage;
import org.bukkit.entity.Player;

public abstract class AbstractRequirement implements Requirement {

    protected final Goldiriath plugin;
    final BukkitMessage message;

    public AbstractRequirement(Goldiriath plugin, BukkitMessage message) {
        this.plugin = plugin;
        this.message = message;
    }

    @Override
    public void apply(Player player) {
        // To be overridden
    }

    @Override
    public BukkitMessage getMessage(Player player) {
        return message;
    }

    // Util methods
    public int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid number: " + string + "!");
        }
    }

}
