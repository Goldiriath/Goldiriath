package net.goldiriath.plugin.game.questing.quest.triggering.trigger;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.game.questing.quest.triggering.TriggerSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class NPCTrigger extends AbstractTrigger {


    private final int npcID;
    private final int radiusSquared;

    public NPCTrigger(TriggerSource source, String[] args) {
        super(source);
        npcID = parseInt(args[1]);
        radiusSquared = parseInt(args[2]);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);

        if (npc == null) {
            return;
        }

        if (!Objects.equals(npc.getStoredLocation().getWorld(), event.getTo().getWorld())) {
            return;
        }

        if (event.getTo().distanceSquared(npc.getStoredLocation()) <= radiusSquared) {
            trigger(event.getPlayer());
        }
    }
}
