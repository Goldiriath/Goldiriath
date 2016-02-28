package net.goldiriath.plugin.mobspawn.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import net.goldiriath.plugin.mobspawn.MobSpawn;
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

    public boolean inflict(Player player, int rawDamage) {
        final UUID uuid = player.getUniqueId();
        Integer inflicted = inflictMap.get(uuid);

        if (inflicted == null) {
            inflicted = 0;
        }

        inflicted += rawDamage;
        inflictMap.put(uuid, inflicted);

        health -= rawDamage;

        return health > 0;
    }

}
