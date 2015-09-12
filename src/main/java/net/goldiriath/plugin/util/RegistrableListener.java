package net.goldiriath.plugin.util;

import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Registrable;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class RegistrableListener implements Registrable, Listener {

    @Getter
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
