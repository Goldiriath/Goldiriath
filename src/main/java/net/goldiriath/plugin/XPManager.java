package net.goldiriath.plugin;

import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.math.HealthMath;
import net.goldiriath.plugin.math.XPMath;
import net.goldiriath.plugin.mobspawn.MobSpawn;
import net.goldiriath.plugin.mobspawn.citizens.MobSpawnTrait;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class XPManager extends AbstractService {

    public XPManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKillMob(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        final EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

        if (!(entityEvent.getDamager() instanceof Player)) {
            return;
        }

        recordKill((Player) entityEvent.getDamager(), event.getEntity());
    }

    private void recordKill(Player player, LivingEntity killed) {

        NPC npc = plugin.msm.getBridge().getNPC(killed);
        if (npc == null) {
            return;
        }

        // Impement for non-mobspawn mobs (just a MobProfile)
        MobSpawnTrait mobSpawnTrait = npc.getTrait(MobSpawnTrait.class);
        if (mobSpawnTrait == null) {
            return;
        }

        final MobSpawn mobSpawn = mobSpawnTrait.getSpawn();
        final int mobLevel = mobSpawn.getProfile().getLevel();

        final PlayerData data = plugin.pm.getData(player);

        final int oldXp = data.getXp();
        final int oldLevel = XPMath.xpToLevel(oldXp);

        final int xpGain = XPMath.xpGainForKill(oldLevel, mobLevel);

        final int newXp = oldXp + xpGain;
        final int newLevel = XPMath.xpToLevel(newXp);

        data.setXp(newXp);

        if (newLevel != oldLevel) {
            // Level health and Mana
            int newMaxHealth = HealthMath.levelToMaxHealth(newLevel);
            plugin.hm.setMaxHealth(player, newMaxHealth);
            data.setMaxMana(newMaxHealth); // TODO: ManaManager?

            // Play effect
            levelUpEffect(player);
            player.sendMessage(ChatColor.YELLOW + "Congratulations on reaching level " + newLevel + "!");
        }
    }

    private void levelUpEffect(Player player) {

        // Firework effect
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        final FireworkMeta meta = fw.getFireworkMeta();

        final FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .with(FireworkEffect.Type.STAR)
                .withColor(Color.RED)
                .withTrail()
                .withFade(Color.WHITE)
                .build();

        meta.addEffect(effect);

        meta.setPower(2); // +- 1 second of flight time
        fw.setFireworkMeta(meta);
    }

}
