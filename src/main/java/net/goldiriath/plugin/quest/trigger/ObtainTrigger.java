package net.goldiriath.plugin.quest.trigger;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.quest.ParseException;
import net.goldiriath.plugin.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ObtainTrigger extends PlayerEventTrigger {

    private final ItemStack item;

    public ObtainTrigger(Goldiriath plugin, String[] args) {
        super(plugin);

        item = Util.parseItem(args[1]);
        if (item == null) {
            throw new ParseException("Unrecognized item: " + args[1]);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!item.equals(event.getItem().getItemStack())) { // TODO: Validate this has correct behaviour
            return;
        }

        trigger(event.getPlayer());
    }

}
