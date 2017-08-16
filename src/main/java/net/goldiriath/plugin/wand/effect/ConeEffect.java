package net.goldiriath.plugin.wand.effect;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;
import thirdparty.de.slikey.effectlib.util.*;

/**
 * Created by koen on 04/08/2017.
 */
public class ConeEffect extends Effect {
    @Setter @Getter
    private ParticleEffect particle = ParticleEffect.FLAME;
    /** callback for later damage implementation */
    private RayEffect.LocationCallback callback;
    /** Growing per iteration in the length (0.05) */
    private float lengthGrow = .05f;
    /** Radials per iteration to spawn the next particle (PI / 16) */
    private double angularVelocity = Math.PI / 16;
    /** Cone-particles per iteration (10) */
    private int particles = 10;
    /** Growth in blocks per iteration on the radius (0.006) */
    private float radiusGrow = 0.006f;
    /** Conesize in particles per cone */
    private int particlesCone = 180;
    /** Start-angle or rotation of the cone */
    private double rotation = 0;
    /** Randomize every cone on creation (false) */
    private boolean randomize = false;
    /** Current step. Works as counter */
    private int step = 0;
    /** the player spitting the cone */
    private Player player;

    public ConeEffect(EffectManager effectManager, Player player) {
        super(effectManager);
        type = EffectType.REPEATING;
        period = 1;
        iterations = 3 * (particlesCone / particles);
        this.player = player;
    }

    @Override
    public void reset() {
        this.step = 0;
    }

    @Override
    public void onRun() {
        Location location = player.getEyeLocation();
        for (int x = 0; x < particles; x++) {
            if (step > particlesCone) {
                step = 0;
            }
            if (randomize && step == 0) {
                rotation = RandomUtils.getRandomAngle();
            }
            double angle = step * angularVelocity + rotation;
            float radius = step * radiusGrow;
            float length = step * lengthGrow;
            Vector v = new Vector(Math.cos(angle) * radius, length, Math.sin(angle) * radius);
            VectorUtils.rotateAroundAxisX(v, (location.getPitch() + 90) * MathUtils.degreesToRadians);
            VectorUtils.rotateAroundAxisY(v, -location.getYaw() * MathUtils.degreesToRadians);

            location.add(v);
            display(particle, location);
            location.subtract(v);
            step++;
        }
    }
}
