package net.goldiriath.plugin.game;

import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerLoginEvent;

public class DevMode extends AbstractService {

    @Getter
    private boolean devMode;

    public DevMode(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    public void setDevMode(boolean enabled) {
        this.devMode = enabled;

        plugin.getServer().getPluginManager().callEvent(new DevModeChangeEvent(enabled));

        if (!enabled) {
            return;
        }

        // Kick all non-staff members
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.hasPermission("goldiriath.devmode.online")) {
                player.kickPlayer("Development mode enabled");
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (devMode && !event.getPlayer().isOp()) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Development mode is currently enabled");
        }
    }

    public static class DevModeChangeEvent extends Event {

        private static final HandlerList handlers = new HandlerList();
        //
        @Getter
        private final boolean devMode;

        public DevModeChangeEvent(boolean enabled) {
            this.devMode = enabled;
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        // Bukkit magic
        public static HandlerList getHandlerList() {
            return handlers;
        }

    }

}
