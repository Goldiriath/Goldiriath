package net.goldiriath.plugin.game.questing.quest.triggering.trigger;

import net.goldiriath.plugin.game.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.game.questing.script.ParseException;
import net.pravian.aero.serializable.SerializableBlockLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class LocationTrigger extends AbstractTrigger {

    private final Location location;
    private final int radiusSquared;

    public LocationTrigger(TriggerSource source, String[] args) {
        super(source);

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
