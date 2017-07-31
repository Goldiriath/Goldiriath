package net.goldiriath.plugin.game.mobspawn.citizens;

import lombok.Getter;
import net.citizensnpcs.api.trait.Trait;
import net.goldiriath.plugin.game.mobspawn.MobSpawnProfile;

public class MobProfileTrait extends Trait {

    public static String NAME = "go-mobprofile";
    //
    @Getter
    private final MobSpawnProfile profile;

    public MobProfileTrait(MobSpawnProfile spawn) {
        super(NAME);
        this.profile = spawn;
    }
}
