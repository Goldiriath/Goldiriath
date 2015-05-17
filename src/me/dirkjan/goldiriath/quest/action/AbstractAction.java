package me.dirkjan.goldiriath.quest.action;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.quest.Quest;

public abstract class AbstractAction implements Action {

    protected final Goldiriath plugin;
    protected final Quest quest;

    public AbstractAction(Quest quest) {
        this.plugin = quest.getManager().getPlugin();
        this.quest = quest;
    }

    public AbstractAction(Goldiriath plugin) {
        this.plugin = plugin;
        this.quest = null;
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
