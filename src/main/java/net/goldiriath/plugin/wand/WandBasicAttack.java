package net.goldiriath.plugin.wand;

import java.util.Collection;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import thirdparty.de.slikey.effectlib.Effect;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;

public class WandBasicAttack extends AbstractService {

    public static double Y_OFFSET = -0.3;
    public static double HIT_RADIUS = 0.4;

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

        basicAttack(event.getPlayer(), event.getItem());
    }

    public void basicAttack(Player player, ItemStack wand) {
        World world = player.getWorld();

        Vector eyeLoc = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection();

        Location origin = eyeLoc.clone().add(direction).toLocation(world);
        origin.setDirection(direction);
        origin.add(0, Y_OFFSET, 0);

        // Play the visual
        Effect effect = new RayEffect(plugin.elb.getManager(), new AttackDamager(player, wand));
        effect.setDynamicOrigin(new DynamicLocation(origin));
        effect.start();

        // Play sound
        player.getWorld().playSound(origin, Sound.ENTITY_TNT_PRIMED, 1.0f, 1.4f);
    }

    public class AttackDamager implements RayEffect.LocationCallback {

        private final Player player;
        private final ItemStack wand;

        public AttackDamager(Player player, ItemStack wand) {
            this.player = player;
            this.wand = wand;
        }

        @Override
        public boolean call(Location location) {
            World w = location.getWorld();

            Collection<Entity> nearby = w.getNearbyEntities(location, HIT_RADIUS, HIT_RADIUS, HIT_RADIUS);
            if (nearby.isEmpty()) {
                return false;
            }

            // Target, do damage
            Entity target = nearby.iterator().next();
            plugin.bm.attack(player, wand, target);

            // Show effect
            SplashEffect effect = new SplashEffect(plugin.elb.getManager());
            effect.setDynamicOrigin(new DynamicLocation(location));
            effect.start();

            // Play sound
            player.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.8f);

            // TODO: Hit ground effect
            return true;
        }

    }

}
