package net.goldiriath.plugin.mobspawn.citizens;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

    public void unapplyTo(NPC npc) {
        if (getType() == TargetType.ENTITY) {
            Goldiriath.instance().pm.getData(player).getBattle().ease(npc);
        }
    }

    public void applyTo(NPC npc) {
        Navigator nav = npc.getNavigator();

        if (getType() == TargetType.ENTITY) {
            nav.setTarget((Entity) player, true);
            Goldiriath.instance().pm.getData(player).getBattle().assail(npc);
        } else {
            nav.setTarget(location);
        }
    }

}
