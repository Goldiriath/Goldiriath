package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

abstract class GoldiriathListener implements EventContainer, Listener {

    protected final Goldiriath plugin;

    public GoldiriathListener(Goldiriath plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public abstract void unregister();
}
