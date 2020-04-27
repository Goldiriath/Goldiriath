package net.goldiriath.plugin.game.wand;

import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;
import thirdparty.de.slikey.effectlib.util.ParticleEffect;

public class FalterEffect extends Effect {

    public static final ParticleEffect PARTICLE = ParticleEffect.SMOKE_NORMAL;
    public static final double MAX_OFFSET = 0.4;
    public static final double Y_OFFSET = -0.2;
    public static final int ITERATIONS = 15;
    public static final int PARTICLES_PER_ITERATION = 3;

    public FalterEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        iterations = 20;
        period = 1;
    }

    @Override
    public void onRun() {
        double offX = Math.random() * MAX_OFFSET;
        double offY = Math.random() * MAX_OFFSET + Y_OFFSET;
        double offZ = Math.random() * MAX_OFFSET;

        for (int i = 0; i < PARTICLES_PER_ITERATION; i++) {
            display(PARTICLE, getLocation().clone().add(offX, offY, offZ));
        }
    }

}
