package net.goldiriath.plugin.quest.action;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;

public abstract class AbstractAction implements Action {

    protected final Goldiriath plugin;

    public AbstractAction(Goldiriath plugin) {
        this.plugin = plugin;
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
