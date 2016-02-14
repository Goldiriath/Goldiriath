package net.goldiriath.plugin.questing.script.item;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.questing.script.InvalidContextException;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.script.Script;
import net.goldiriath.plugin.questing.script.ScriptContext;
import org.bukkit.entity.Player;

public abstract class ScriptItem {

    protected final Goldiriath plugin;
    protected final Script script;
    //
    protected int delay = 0;

    public ScriptItem(Script script) {
        this.plugin = script.getPlugin();
        this.script = script;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Script getScript() {
        return script;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public abstract void execute(Player player);

    // util
    protected final ScriptContext context() {
        return script.getContext();
    }

    protected final void context(ScriptContext.ScriptContextType type) {
        if (script.getContext().getType() != type) {
            invalidContext();
        }
    }

    protected final void invalidContext() {
        throw new InvalidContextException(script.getContext().getType());
    }

    protected final int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid number: " + string + "!");
        }
    }

}
