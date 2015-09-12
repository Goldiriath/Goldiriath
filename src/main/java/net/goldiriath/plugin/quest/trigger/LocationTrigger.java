package net.goldiriath.plugin.quest.trigger;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;
import net.pravian.bukkitlib.serializable.SerializableBlockLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class LocationTrigger extends PlayerEventTrigger {

    private final Location location;
    private final int radiusSquared;

    public LocationTrigger(Goldiriath plugin, String[] args) {
        super(plugin);

        this.location = new SerializableBlockLocation(args[1]).deserialize();
        if (location == null) {
            throw new ParseException("Invalid location: " + args[1]);
        }

        this.radiusSquared = (int) Math.pow(parseInt(args[2]), 2);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getTo().getWorld().equals(location.getWorld())) {
            return;
        }

        if (event.getTo().distanceSquared(location) <= radiusSquared) {
            trigger(event.getPlayer());
        }
    }

}
