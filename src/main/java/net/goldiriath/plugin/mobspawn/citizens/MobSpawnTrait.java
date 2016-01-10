package net.goldiriath.plugin.mobspawn.citizens;

import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import net.goldiriath.plugin.mobspawn.MobSpawn;

public class MobSpawnTrait extends Trait {

    public static String NAME = "mobspawn";
    //
    @Getter
    private final MobSpawn spawn;

    public MobSpawnTrait(MobSpawn spawn) {
        super(NAME);
        this.spawn = spawn;
    }
}
