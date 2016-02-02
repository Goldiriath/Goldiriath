package net.goldiriath.plugin.questing.quest.triggering.trigger;

import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.util.Util;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ObtainTrigger extends AbstractTrigger {

    private final ItemStack item;

    public ObtainTrigger(TriggerSource source, String[] args) {
        super(source);

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
