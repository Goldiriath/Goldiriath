package net.goldiriath.plugin.wand;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.player.info.InfoBattle;
import net.goldiriath.plugin.util.service.AbstractService;
import net.goldiriath.plugin.wand.damager.BasicAttackDamager;
import net.goldiriath.plugin.wand.damager.STAttackDamager;
import net.goldiriath.plugin.wand.effect.RayEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;

public class WandBasicAttack extends AbstractService {

    private static final double Y_OFFSET = -0.3;
    private static final double HIT_RADIUS = 0.4;
    private static final double ATTACK_SPEED = 0.8; // hits/second

    public WandBasicAttack(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(ignoreCancelled = false)
    public void onWandAttack(PlayerInteractEvent event) {
        if (!event.hasItem() || !InventoryUtil.isWand(event.getItem())) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR
                && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        InfoBattle info = plugin.pm.getData(player).getBattle();

        // Limit fire rate
        long time = System.nanoTime();
        if (time - info.getLastWandUse() < (1 / ATTACK_SPEED) * 1_000_000_000) {
            return;
        }
        info.setLastWandUse(time);

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(ATTACK_SPEED);
        basicAttack(player, event.getItem());

        event.setCancelled(true);
    }

    private void basicAttack(Player player, ItemStack wand) {
        World world = player.getWorld();

        Vector eyeLoc = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection();

        Location origin = eyeLoc.clone().add(direction).toLocation(world);
        origin.setDirection(direction);
        origin.add(0, Y_OFFSET, 0);

        // Play the visual
        Effect effect = new RayEffect(plugin.elb.getManager(), new BasicAttackDamager(player, wand, HIT_RADIUS));
        effect.setDynamicOrigin(new DynamicLocation(origin));
        effect.start();

        // Play sound
        player.getWorld().playSound(origin, Sound.ENTITY_TNT_PRIMED, 1.0f, 1.4f);
    }
}
