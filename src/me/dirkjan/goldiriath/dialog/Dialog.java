package me.dirkjan.goldiriath.dialog;

import me.dirkjan.goldiriath.dialog.script.Script;
import me.dirkjan.goldiriath.player.PlayerManager;
import me.dirkjan.goldiriath.quest.requirement.RequirementList;
import me.dirkjan.goldiriath.quest.requirement.RequirementParser;
import me.dirkjan.goldiriath.quest.trigger.PlayerEventTrigger;
import me.dirkjan.goldiriath.quest.trigger.Triggerable;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Dialog implements Triggerable<Player>, ConfigLoadable, Validatable {

    private final DialogContainer dialog;
    private final String id;
    private final PlayerManager pm;
    //
    private boolean once = false;
    private final RequirementList requirements = new RequirementList();
    private Script script;

    public Dialog(DialogContainer dialog, String id) {
        this.dialog = dialog;
        this.id = id;
        this.pm = dialog.getManager().getPlugin().pm;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        once = config.getBoolean("once", false);

        requirements.clear();
        requirements.addAll(new RequirementParser(dialog.getManager().getPlugin(), id)
                .parse(config.getStringList("requirements")));

    }

    public boolean canTrigger(Player player) {
        if (once && pm.getData(player).hasPlayedDialog(id)) {
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

        pm.getData(player).recordPlayDialog(id);

        script.execute(player);
    }

    public String getId() {
        return id;
    }

    public DialogContainer getDialogContainer() {
        return dialog;
    }

    @Override
    public boolean isValid() {
        return dialog != null
                && requirements != null
                && id != null;
    }
}
