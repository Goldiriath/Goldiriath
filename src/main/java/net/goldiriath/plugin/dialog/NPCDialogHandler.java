package net.goldiriath.plugin.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.goldiriath.plugin.util.RegistrableListener;
import net.goldiriath.plugin.quest.ParseException;
import net.goldiriath.plugin.quest.trigger.PlayerEventTrigger;
import net.goldiriath.plugin.quest.trigger.Triggerable;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.pravian.bukkitlib.util.ChatUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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

        // Meta
        npcId = config.getInt("id");
        npcName = ChatUtils.colorize(config.getString("name", ""));

        // Load dialogs
        // Loading is seperated from parsing so dialog script 'zap' commands can find other dialogs
        dialogs.clear();
        final ConfigurationSection dialogsSection = config.getConfigurationSection("dialogs");
        if (dialogsSection != null) {
            for (String dialogId : dialogsSection.getKeys(false)) {

                if (!dialogsSection.isConfigurationSection(dialogId)) {
                    logger.warning("[" + id + "] Skipping dialog '" + dialogId + "'. Invalid format!");
                    continue;
                }

                dialogs.put(dialogId.toLowerCase(), new Dialog(this, dialogId.toLowerCase()));
            }
        }

        // Load options
        // Loading is seperated from parsing so that dialog "option" commands can find options
        options.clear();
        final ConfigurationSection optionsSection = config.getConfigurationSection("options");
        if (optionsSection != null) {
            for (String optionId : optionsSection.getKeys(false)) {

                if (!optionsSection.isConfigurationSection(optionId)) {
                    logger.warning("[" + id + "] Skipping option '" + optionId + "'. Invalid format!");
                    continue;
                }

                options.put(optionId.toLowerCase(), new OptionSet(this, optionId.toLowerCase()));
            }

        }

        // Parse dialogs
        for (Dialog dialog : dialogs.values()) {
            final ConfigurationSection loopDialogSection = config.getConfigurationSection("dialogs." + dialog.getId());

            if (loopDialogSection == null) { // prob capatalisation
                logger.warning("[" + id + "] Skipping dialog '" + dialog.getId() + "'. Invalid dialog ID!");
                dialogs.remove(dialog.getId());
                continue;
            }

            try {
                dialog.loadFrom(loopDialogSection);
            } catch (ParseException ex) {
                logger.warning("[" + id + "] Skipping dialog '" + dialog.getId() + "'. Could not parse Dialog.");
                logger.warning(ExceptionUtils.getFullStackTrace(ex));
                continue;
            }

            if (!dialog.isValid()) {
                logger.warning("[" + id + "] Skipping dialog '" + dialog.getId() + "'. Missing values!");
                dialogs.remove(dialog.getId());
            }
        }

        // Parse options
        for (OptionSet option : options.values()) {
            final ConfigurationSection loopOptionSection = config.getConfigurationSection("options." + option.getId());

            if (loopOptionSection == null) { // prob capatalisation
                logger.warning("[" + id + "] Skipping option '" + option.getId() + "'. Invalid option ID!");
                options.remove(option.getId());
                continue;
            }

            try {
                option.loadFrom(loopOptionSection);
            } catch (ParseException ex) {
                logger.warning("[" + id + "] Skipping option '" + option.getId() + "'. Could not parse OptionSet.");
                logger.warning(ExceptionUtils.getFullStackTrace(ex));
                options.remove(option.getId());
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

        // Register events
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
                && npcId != 0
                && npcName != null
                && !npcName.isEmpty();
    }

}
