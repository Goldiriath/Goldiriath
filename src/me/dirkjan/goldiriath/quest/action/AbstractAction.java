package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;

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
