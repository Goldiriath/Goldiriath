package net.goldiriath.plugin.player.info;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.mobspawn.citizens.CitizensBridge;
import net.goldiriath.plugin.player.PlayerData;

public class InfoBattle extends Info {

    private final Set<UUID> assailants = new HashSet<>();

    public InfoBattle(PlayerData data) {
        super(data);
    }

    public void assail(NPC mob) {
        assailants.add(mob.getUniqueId());
    }

    public void ease(NPC mob) {
        assailants.remove(mob.getUniqueId());
    }

    public Set<UUID> getAssailingNpcs() {
        clean();
        return assailants;
    }

    private void clean() {
        CitizensBridge cb = plugin.msm.getBridge();

        for (Iterator<UUID> it = assailants.iterator(); it.hasNext();) {
            NPC npc = cb.getNPC(it.next());
            if (npc == null || !npc.isSpawned()) {
                it.remove();
            }
        }
    }

}
