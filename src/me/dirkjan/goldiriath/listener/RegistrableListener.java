package me.dirkjan.goldiriath.listener;

import me.dirkjan.goldiriath.util.Registrable;
import me.dirkjan.goldiriath.Goldiriath;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class RegistrableListener implements Registrable, Listener {

    protected final Goldiriath plugin;

    public RegistrableListener(Goldiriath plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        HandlerList.unregisterAll(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
