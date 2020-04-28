package net.goldiriath.plugin.game.wand;

import org.bukkit.Location;
import org.bukkit.Particle;
import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;

public class SplashEffect extends Effect {

    public static final Particle PARTICLE = Particle.REDSTONE;
    public static final int RAYS = 3;
    public static final double DENSITY = 30; // In particles/m
    public static final double MIN_RAY_LENGTH = 0.7;
    public static final double MAX_RAY_LENGTH = 1.7; // In m

    public SplashEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.INSTANT;
    }

    @Override
    public void onRun() {
        Location origin = getLocation().clone();

        // Splash against a surface, in the opposite direction
        origin.setDirection(origin.getDirection().multiply(-1));
        origin.add(origin.getDirection().clone().multiply(0.5));

        for (int i = 0; i < RAYS; i++) {
            Location target = origin.clone();
            target.setPitch((float) (Math.random() * 90 - 45));
            target.setYaw((float) (target.getYaw() + (Math.random() * 90 - 45)));

            // Calculate ray properties
            double rayLength = MIN_RAY_LENGTH + Math.random() * (MAX_RAY_LENGTH - MIN_RAY_LENGTH);
            int particles = (int) (DENSITY * rayLength);
            double particleSpacing = rayLength / (particles - 1);

            // Cast the ray
            double distance = 0;
            for (int j = 0; j < particles; j++) {
                Location particleLoc = target.clone();
                particleLoc.add(particleLoc.getDirection().multiply(distance));

                display(PARTICLE, particleLoc);

                distance += particleSpacing;
            }

            display(PARTICLE, origin, 0.5f, 2);
        }
    }

}
