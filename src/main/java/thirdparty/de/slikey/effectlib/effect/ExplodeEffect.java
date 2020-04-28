package thirdparty.de.slikey.effectlib.effect;

import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.EffectManager;
import thirdparty.de.slikey.effectlib.EffectType;
import org.bukkit.Particle;
import thirdparty.de.slikey.effectlib.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Sound;

public class ExplodeEffect extends Effect {

    public Particle particle1 = Particle.EXPLOSION_NORMAL;
    public Particle particle2 = Particle.EXPLOSION_HUGE;

    /**
     * Amount of spawned smoke-sparks
     */
    public int amount = 25;
    public Sound sound = Sound.ENTITY_GENERIC_EXPLODE;

    public ExplodeEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.INSTANT;
        speed = 0.5f;
    }

    @Override
    public void onRun() {
        Location location = getLocation();
        location.getWorld().playSound(location, sound, 4.0F, (1.0F + (RandomUtils.random.nextFloat() - RandomUtils.random.nextFloat()) * 0.2F) * 0.7F);
        display(particle1, location);
        display(particle2, location);
    }

}
