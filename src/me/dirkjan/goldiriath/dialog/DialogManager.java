package me.dirkjan.goldiriath.dialog;

import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.util.Service;
import net.pravian.bukkitlib.config.YamlConfig;

public class DialogManager implements Service {

    private final Goldiriath plugin;
    private final YamlConfig config;

    public DialogManager(Goldiriath plugin) {
        this.plugin = plugin;
        this.config = new YamlConfig(plugin, "dialogs.yml", false);
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
    }

    @Override
    public void stop() {
    }

}
