package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.modifier.HealthOverTimeEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;

public class SwordDivineLight extends ActiveSkill {

    public SwordDivineLight(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        // Single target, heal over time
        // 3 times
        // 1 sec delay
        // 10 health per heal

        track(player.launchProjectile(Snowball.class));
    }

    public void track(final Snowball sb) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!sb.isValid() || sb.isDead() || sb.isOnGround()) {
                    cancel();
                }

                for (Entity e : sb.getNearbyEntities(1, 1, 1)) {
                    if (!(e instanceof Player) || e.equals(sb.getShooter())) {
                        continue;
                    }
                    cancel();

                    // Add a healing effect
                    PlayerData playerData = plugin.pm.getData(player);
                    double healing = 0.08*playerData.getMaxHealth()+0.16*(playerData.getMaxHealth()-playerData.getHealth());
                    int amount = (int) healing/8;
                    plugin.pm.getData((Player) e).getModifiers().addModifier(new HealthOverTimeEffect(8 * 20, 1 * 20, amount));
                    return;
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

}
