package net.goldiriath.plugin.game.questing.dialog;

import net.goldiriath.plugin.player.PlayerManager;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementList;
import net.goldiriath.plugin.game.questing.quest.requirement.RequirementParser;
import net.goldiriath.plugin.game.questing.script.Script;
import net.goldiriath.plugin.game.questing.script.ScriptContext;
import net.goldiriath.plugin.game.questing.script.ScriptParser;
import net.goldiriath.plugin.game.questing.quest.triggering.Triggerable;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Dialog implements Triggerable<Player>, ConfigLoadable, Validatable {

    private final NPCDialogHandler handler;
    private final String id;
    private final PlayerManager pm;
    //
    private final RequirementList requirements = new RequirementList();
    private Script script;

    public Dialog(NPCDialogHandler dialog, String id) {
        this.handler = dialog;
        this.id = id;
        this.pm = dialog.getManager().getPlugin().pm;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {

        // Requirements
        requirements.clear();
        RequirementParser rParser = new RequirementParser(handler.getPlugin(), id);
        requirements.addAll(rParser.parse(config.getStringList("requirements")));

        // Script
        ScriptContext sContext = new ScriptContext(this);
        ScriptParser sParser = new ScriptParser(handler.getPlugin(), sContext);
        script = sParser.parse(config.getStringList("script"));
    }

    public boolean canTrigger(Player player) {
        return requirements.has(player);
    }

    @Override
    public void onTrigger(Player player) {
        if (script == null) {
            return;
        }

        // Player data handles dialog logic
        pm.getData(player).getDialogs().start(this);
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
