package net.goldiriath.plugin.player.info;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.game.citizens.CitizensBridge;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.Util;

public class InfoBattle extends Info {

    public static long HEALTH_DELAY_TICKS = 5 * 20L;
    //
    private final Set<UUID> assailants = new HashSet<>();
    private long lastAutoHeal = 0;
    @Getter
    @Setter
    private long lastWandUse = 0;

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
        cleanAssailing();
        return Collections.unmodifiableSet(assailants);
    }

    public void autoHeal() {
        long diff = Util.getServerTick() - lastAutoHeal;
        if (assailants.size() > 0) {
            diff /= 4;
        }

        if (diff < HEALTH_DELAY_TICKS) {
            return;
        }

        if (data.getHealth() == 0) {
            return;
        }

        // 1% health increase
        int healAmount = (int) 0.01 * data.getMaxHealth();
        if (healAmount < 1) {
            healAmount = 1;
        }

        // Heal the player
        lastAutoHeal = Util.getServerTick();
        plugin.dam.heal(data.getPlayer(), healAmount);
    }

    private void cleanAssailing() {
        for (Iterator<UUID> it = assailants.iterator(); it.hasNext();) {
            NPC npc = plugin.czb.getNPC(it.next());
            if (npc == null || !npc.isSpawned()) {
                it.remove();
            }
        }
    }

}
