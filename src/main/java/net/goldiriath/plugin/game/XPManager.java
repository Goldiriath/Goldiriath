package net.goldiriath.plugin.game;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.citizens.HostileMobTrait;
import net.goldiriath.plugin.math.HealthMath;
import net.goldiriath.plugin.math.XPMath;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    public void onPlayerKillMob(NPCDeathEvent event) {
        NPC npc = event.getNPC();

        if (!(npc.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        final EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) npc.getEntity().getLastDamageCause();

        if (!(entityEvent.getDamager() instanceof Player)) {
            return;
        }
        final Player player = (Player) entityEvent.getDamager();

        HostileMobTrait trait = npc.getTrait(HostileMobTrait.class);
        if (trait == null) {
            return;
        }

        // Zero minecraft XP is dropped
        event.setDroppedExp(0);

        final PlayerData data = plugin.pym.getData(player);

        final int oldXp = data.getXp();
        final int oldLevel = XPMath.xpToLevel(oldXp);
        final int xpGain = XPMath.xpGainForKill(oldLevel, trait.getProfile().getLevel());
        final int newXp = oldXp + xpGain;
        final int newLevel = XPMath.xpToLevel(newXp);

        data.setXp(newXp);

        // Levelling
        if (newLevel != oldLevel) {
            // Level health and Mana
            int newMaxHealth = HealthMath.levelToMaxHealth(newLevel);
            plugin.pym.getData(player).setMaxHealth(newMaxHealth);

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
