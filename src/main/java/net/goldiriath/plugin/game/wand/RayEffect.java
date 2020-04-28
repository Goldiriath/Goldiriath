package net.goldiriath.plugin.game.wand;

import org.bukkit.Location;
import org.bukkit.Particle;
import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;

public class RayEffect extends Effect {

    public static final double RANGE = 15; // In m
    public static final double SPEED = 30; // In m/s
    public static final double DENSITY = 15; // In particles/m
    public static final Particle PARTICLE = Particle.FLAME;

    private final LocationCallback locationCallback;
    private double lastParticlePos; // In [0, RANGE]

    public RayEffect(EffectManager effectManager, LocationCallback callback) {
        super(effectManager);
        this.locationCallback = callback;
        type = EffectType.REPEATING;
        reset();
    }

    @Override
    public void reset() {
        lastParticlePos = 0;
        iterations = 2;
        period = 1;
    }

    @Override
    public void onRun() {
        // Spacing between particles
        double particleSpacing = 1 / DENSITY;

        // Distance covered during this tick
        double distanceCovered = SPEED * 0.05;

        // Display all the particles we're covering this tick
        double startPos = lastParticlePos;
        for (double p = 0; p <= distanceCovered; p += particleSpacing) {
            double particlePos = startPos + p;

            if (particlePos > RANGE) {
                break;
            }

            // Calculate the position of the particle, and display it
            display(PARTICLE, toLocation(particlePos));
            lastParticlePos = particlePos;
        }

        boolean cancel = locationCallback.call(toLocation(lastParticlePos));

        // Should we continue next tick?
        if (!cancel && lastParticlePos + particleSpacing <= RANGE) {
            iterations = 5;
            return;
        }

        // Effect is over
        if (!cancel) {
            // We're out of range, display falter effect
            FalterEffect effect = new FalterEffect(effectManager);
            effect.setDynamicOrigin(new DynamicLocation(toLocation(lastParticlePos)));
            effect.start();
        }

        iterations = 1;
    }

    private Location toLocation(double pos) {
        Location loc = getLocation();
        return loc.clone().add(loc.getDirection().clone().normalize().multiply(pos));
    }

    public static interface LocationCallback {

        public boolean call(Location location);
    }
}
