package net.goldiriath.plugin.game.questing.dialog;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Plugins;
import org.apache.commons.lang.exception.ExceptionUtils;

public class DialogManager extends AbstractService {

    private final File dialogContainer;
    private final Map<String, NPCDialogHandler> handlers;

    public DialogManager(Goldiriath plugin) {
        super(plugin);
        this.dialogContainer = Plugins.getPluginFile(plugin, "dialogs");
        this.handlers = new HashMap<>();
    }

    @Override
    protected void onInit() {
        // Ensure folder is present
        if (dialogContainer.isFile()) {
            if (!dialogContainer.delete()) {
                logger.severe("Not loading dialogs! Could not delete file: " + dialogContainer.getAbsolutePath());
                return;
            }
        }
        if (!dialogContainer.exists()) {
            dialogContainer.mkdirs();
        }

        // Preload dialogs
        handlers.clear();
        for (File file : dialogContainer.listFiles(new DialogFileFilter(plugin))) {
            String id = parseDialogId(file);

            if (id.isEmpty() || handlers.containsKey(id)) {
                logger.warning("Skipping dialog handler file: " + file.getName() + ". Invalid dialog ID!");
                continue;
            }

            handlers.put(id, new NPCDialogHandler(this, id));
        }
    }

    @Override
    protected void onStart() {
        if (dialogContainer.isFile()) {
            return;
        }

        // Load handlers
        for (File file : dialogContainer.listFiles(new DialogFileFilter(plugin))) {
            final String id = parseDialogId(file);
            final NPCDialogHandler handler = handlers.get(id);
            if (handler == null) {
                // Assume onInit() already printed an error
                continue;
            }

            final YamlConfig config = new YamlConfig(plugin, file, false);
            config.load();

            try {
                handler.loadFrom(config);
            } catch (Exception ex) {
                logger.warning("Skipping dialog handler: " + id + ". Exception loading dialog!");
                logger.severe(ExceptionUtils.getFullStackTrace(ex));
            }

            if (!handler.isValid()) {
                logger.warning("Skipping dialog handler: " + id + ". Invalid dialog handler! (Are there missing entries?)");
                continue;
            }

            handlers.put(id, handler);
        }

        int dialogs = 0;
        for (NPCDialogHandler handler : handlers.values()) {
            dialogs += handler.getDialogs().size();
        }
        logger.info("Loaded " + handlers.size() + " NPC dialog handlers for " + dialogs + " dialogs");
    }

    private String parseDialogId(File file) {
        return file.getName().replace("dialog_", "").replace(".yml", "").trim().toLowerCase();
    }

    @Override
    protected void onStop() {
        for (NPCDialogHandler handler : handlers.values()) {
            handler.unregister();
        }
    }

    public Map<String, NPCDialogHandler> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

}
