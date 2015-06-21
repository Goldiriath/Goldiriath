package me.dirkjan.goldiriath.dialog;

import java.util.HashMap;
import java.util.Map;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.Service;
import net.pravian.bukkitlib.config.YamlConfig;

public class DialogManager implements Service {

    private final Goldiriath plugin;
    private final YamlConfig config;
    private final Map<String, NPCDialogHandler> handlers;

    public DialogManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.config = new YamlConfig(plugin, "dialogs.yml", true);
        this.handlers = new HashMap<>();
    }

    public Goldiriath getPlugin() {
        return plugin;
    }

    @Override
    public void start() {
        if (!config.exists()) {
            return;
        }

        config.load();

        handlers.clear();
        for (String handlerId : config.getKeys(false)) {

            if (!config.isConfigurationSection(handlerId)) {
                plugin.getLogger().warning("Ignoring dialog handler: '" + handlerId + "'. Invalid format!");
                continue;
            }

            final NPCDialogHandler handler = new NPCDialogHandler(this, handlerId.toLowerCase());
            handler.loadFrom(config.getConfigurationSection(handlerId.toLowerCase()));

            if (!handler.isValid()) {
                plugin.getLogger().warning("Ignoring dialog handler: '" + handlerId + "'. Missing values!");
                continue;
            }

            handlers.put(handlerId.toLowerCase(), handler);
        }

    }

    @Override
    public void stop() {
        for (NPCDialogHandler handler : handlers.values()) {
            handler.unregister();
        }
    }

    public Map<String, NPCDialogHandler> getHandlers() {
        return handlers;
    }

}
