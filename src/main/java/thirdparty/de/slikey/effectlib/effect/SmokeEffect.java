package thirdparty.de.slikey.effectlib.effect;

import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;
import org.bukkit.Particle;
import thirdparty.de.slikey.effectlib.util.RandomUtils;
import org.bukkit.Location;

public class SmokeEffect extends Effect {

    /**
     * ParticleType of spawned particle
     */
    public Particle particle = Particle.SMOKE_NORMAL;

    /**
     * Number of particles to display
     */
    public int particles = 20;

    public SmokeEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        period = 1;
        iterations = 300;
    }

    @Override
    public void onRun() {
        Location location = getLocation();
        for (int i = 0; i < particles; i++) {
            location.add(RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * 0.6d));
            location.add(0, RandomUtils.random.nextFloat() * 2, 0);
            display(particle, location);
        }
    }

}
