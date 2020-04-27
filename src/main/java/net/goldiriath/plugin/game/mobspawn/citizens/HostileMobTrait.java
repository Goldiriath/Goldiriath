package net.goldiriath.plugin.game.mobspawn.citizens;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import net.goldiriath.plugin.game.mobspawn.MobSpawnProfile;
import org.bukkit.entity.Player;

public class HostileMobTrait extends Trait {

    public static String NAME = "go-hostilemob";
    //
    @Getter
    private final int maxHealth;
    //
    @Getter
    private MobSpawnProfile profile;
    @Getter
    private int health;
    @Getter
    private UUID lastAttacker;
    private final Map<UUID, Integer> inflictMap = new HashMap<>();

    public HostileMobTrait(MobSpawnProfile profile) {
        super(NAME);
        this.profile = profile;
        this.maxHealth = profile.getHealth();
        this.health = profile.getHealth();

    }

    public int getInflictedDamage(Player player) {
        return inflictMap.getOrDefault(player.getUniqueId(), 0);
    }

    public boolean inflict(Player player, int effectiveDamage) {
        final UUID uuid = player.getUniqueId();
        Integer inflicted = inflictMap.get(uuid);

        if (inflicted == null) {
            inflicted = 0;
        }

        inflicted += effectiveDamage;
        inflictMap.put(uuid, inflicted);

        health -= effectiveDamage;

        lastAttacker = uuid;

        return health > 0;
    }

}
