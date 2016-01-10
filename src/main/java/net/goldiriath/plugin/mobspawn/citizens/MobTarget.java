package net.goldiriath.plugin.mobspawn.citizens;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MobTarget {

    @Getter
    private final Player player;
    @Getter
    private final Location location;

    public MobTarget(Player player) {
        Preconditions.checkNotNull(player);
        this.player = player;
        this.location = null;
    }

    public MobTarget(Location location) {
        Preconditions.checkNotNull(location);
        this.player = null;
        this.location = location;
    }

    public TargetType getType() {
        return player != null ? TargetType.ENTITY : TargetType.LOCATION;
    }

    public void applyTo(NPC npc) {
        if (getType() == TargetType.ENTITY) {
            npc.getNavigator().setTarget((Entity) player, true);
        } else {
            npc.getNavigator().setTarget(location);
        }
    }

}
