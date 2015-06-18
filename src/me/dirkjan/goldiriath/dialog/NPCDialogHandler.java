package me.dirkjan.goldiriath.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.listener.RegistrableListener;
import me.dirkjan.goldiriath.quest.trigger.PlayerEventTrigger;
import me.dirkjan.goldiriath.quest.trigger.Triggerable;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Validatable;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class NPCDialogHandler extends RegistrableListener implements Triggerable<Player>, ConfigLoadable, Validatable {

    private final DialogManager dm;
    private final String id;
    private final Logger logger;
    //
    private int npcId;
    private String npcName;
    private final Map<String, Dialog> dialogs = new HashMap<>();
    private final List<Dialog> clickDialogs = new ArrayList<>();
    private final Map<String, OptionSet> options = new HashMap<>();

    public String getId() {
        return id;
    }

    public DialogManager getManager() {
        return dm;
    }

    public NPCDialogHandler(DialogManager dm, String id) {
        super(dm.getPlugin());
        this.dm = dm;
        this.id = id;
        this.logger = dm.getPlugin().getLogger();
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        unregister();

        if (!config.isConfigurationSection("dialogs")) {
            logger.warning("[" + id + "] Skipping NPC dialogs '" + id + "'. No dialogs present!");
            return;
        }

        // Load dialogs
        // Loading is seperated from parsing so dialog script 'zap' commands can find other dialogs
        dialogs.clear();
        for (String dialogId : config.getConfigurationSection("dialogs").getKeys(false)) {

            if (!config.isConfigurationSection(dialogId)) {
                logger.warning("[" + id + "] Skipping dialog '" + dialogId + "'. Invalid format!");
                continue;
            }

            final Dialog dialog = new Dialog(this, dialogId.toLowerCase());
            dialogs.put(dialogId.toLowerCase(), dialog);
        }

        // Parse dialogs
        for (Dialog dialog : dialogs.values()) {
            final ConfigurationSection dialogSection = config.getConfigurationSection(dialog.getId());

            if (dialogSection == null) {
                if (!config.isConfigurationSection(dialog.getId())) {
                    logger.warning("[" + id + "] Skipping dialog '" + dialog.getId() + "'. Invalid ID!");
                    continue;
                }
            }

            dialog.loadFrom(dialogSection);

            if (!dialog.isValid()) {
                logger.warning("[" + id + "] Skipping dialog '" + dialog.getId() + "'. Missing values!");
                dialogs.remove(dialog.getId());
            }
        }

        // Load and parse click triggers
        clickDialogs.clear();
        for (String dialogId : config.getStringList("click")) {
            dialogId = dialogId.toLowerCase();

            final Dialog dialog = dialogs.get(dialogId);

            if (dialog == null) {
                logger.warning("[" + id + "] Skipping dialog trigger '" + dialogId + "'. Unknown dialog!");
                continue;
            }

            clickDialogs.add(dialog);
        }

        // Load options
        options.clear();
        if (config.isConfigurationSection("options")) {
            for (String optionId : config.getConfigurationSection("options").getKeys(false)) {
                optionId = optionId.toLowerCase();

                if (!config.isConfigurationSection("options." + optionId)) {
                    logger.warning("[" + id + "] Could not parse option '" + optionId + "'. Invalid format!");
                }

                final OptionSet optionSet = new OptionSet(this, optionId.toLowerCase());
                optionSet.loadFrom(config.getConfigurationSection("options." + optionId));

                options.put(optionId.toLowerCase(), optionSet);
            }

        }

        register();

    }

    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
        if (event.getNPC().getId() != npcId) {
            return;
        }

        onTrigger(null, event.getClicker());
        event.setCancelled(true);
    }

    @Override
    public void onTrigger(PlayerEventTrigger trigger, Player player) {
        for (Dialog dialog : clickDialogs) {
            if (!dialog.canTrigger(player)) {
                continue;
            }

            logger.info("Debug: Triggering dialog " + dialog.getId());
            dialog.onTrigger(trigger, player);
            return;
        }
    }

    public int getNpcId() {
        return npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    public Map<String, Dialog> getDialogsMap() {
        return Collections.unmodifiableMap(dialogs);
    }

    public Collection<Dialog> getDialogs() {
        return getDialogsMap().values();
    }

    public List<Dialog> getClickDialogs() {
        return Collections.unmodifiableList(clickDialogs);
    }

    public Map<String, OptionSet> getOptionsMap() {
        return Collections.unmodifiableMap(options);
    }

    public Collection<OptionSet> getOptions() {
        return getOptionsMap().values();
    }

    @Override
    public boolean isValid() {
        return dm != null
                && id != null
                && npcId != 0;
    }

}
