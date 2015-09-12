package net.goldiriath.plugin.dialog;

import net.goldiriath.plugin.dialog.script.Script;
import net.goldiriath.plugin.dialog.script.ScriptParser;
import net.goldiriath.plugin.player.PlayerManager;
import net.goldiriath.plugin.quest.requirement.RequirementList;
import net.goldiriath.plugin.quest.requirement.RequirementParser;
import net.goldiriath.plugin.quest.trigger.PlayerEventTrigger;
import net.goldiriath.plugin.quest.trigger.Triggerable;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Dialog implements Triggerable<Player>, ConfigLoadable, Validatable {

    private final NPCDialogHandler handler;
    private final String id;
    private final PlayerManager pm;
    //
    private boolean once = false;
    private final RequirementList requirements = new RequirementList();
    private Script script;

    public Dialog(NPCDialogHandler dialog, String id) {
        this.handler = dialog;
        this.id = id;
        this.pm = dialog.getManager().getPlugin().pm;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        once = config.getBoolean("once", false);

        requirements.clear();
        requirements.addAll(new RequirementParser(handler.getManager().getPlugin(), id)
                .parse(config.getStringList("requirements")));

        script = new ScriptParser(this).parse(config.getStringList("script"));
    }

    public boolean canTrigger(Player player) {
        if (once && pm.getData(player).hasHadDialog(id)) {
            return false;
        }

        return requirements.has(player);
    }

    @Override
    public void onTrigger(PlayerEventTrigger trigger, Player player) {
        if (script == null) {
            return;
        }

        if (!canTrigger(player)) {
            return;
        }

        // Player data handles dialog logic
        pm.getData(player).startDialog(this);
    }

    public String getId() {
        return id;
    }

    public NPCDialogHandler getHandler() {
        return handler;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Script getScript() {
        return script;
    }

    @Override
    public boolean isValid() {
        return handler != null
                && requirements != null
                && script != null
                && id != null;
    }
}
