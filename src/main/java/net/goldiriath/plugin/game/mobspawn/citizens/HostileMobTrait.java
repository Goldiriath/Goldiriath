package net.goldiriath.plugin.game.mobspawn.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;

public class HostileMobTrait extends Trait {

    public static String NAME = "go-hostilemob";
    //
    @Getter
    private final int maxHealth;
    //
    @Getter
    private int health;
    private final Map<UUID, Integer> inflictMap = new HashMap<>();

    public HostileMobTrait(int health) {
        super(NAME);
        this.maxHealth = health;
        this.health = health;
    }

    public int getInflictedDamage(Player player) {
        Integer damage = inflictMap.get(player.getUniqueId());
        return damage == null ? 0 : damage;
    }

    public boolean inflict(Player player, int trueDamage) {
        final UUID uuid = player.getUniqueId();
        Integer inflicted = inflictMap.get(uuid);

        if (inflicted == null) {
            inflicted = 0;
        }

        inflicted += trueDamage;
        inflictMap.put(uuid, inflicted);

        health -= trueDamage;

        return health > 0;
    }

}
