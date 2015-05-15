package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class SimpleListener implements EventContainer, Listener {

    protected final Goldiriath plugin;

    public SimpleListener(Goldiriath plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
