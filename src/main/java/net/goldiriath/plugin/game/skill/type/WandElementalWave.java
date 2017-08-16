package net.goldiriath.plugin.game.skill.type;

import net.goldiriath.plugin.game.skill.SkillMeta;
import net.goldiriath.plugin.player.info.InfoWand;
import net.goldiriath.plugin.wand.damager.ElementalWaveNoneDamager;
import net.goldiriath.plugin.wand.effect.ConeEffect;
import net.goldiriath.plugin.wand.effect.RayEffect;
import net.goldiriath.plugin.wand.effect.WaveEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;
import thirdparty.de.slikey.effectlib.util.ParticleEffect;

/**
 * Created by koen on 04/08/2017.
 */
public class WandElementalWave extends ActiveSkill {
    public WandElementalWave(SkillMeta meta, Player player) {
        super(meta, player);
    }

    @Override
    public void use() {
        ItemStack firstItem = player.getInventory().getItem(0);
        if (!(firstItem.getType() == Material.EMERALD)) {
            return;
        }
        InfoWand wand = plugin.pm.getData(player).getWand();
        Vector eyeLoc = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection();

        Location origin = eyeLoc.clone().add(direction).toLocation(player.getWorld());
        origin.setDirection(direction);

        // Play sound
        player.getWorld().playSound(origin, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.4f);

        switch (wand.getWandElement()) {
            case Air:
                //play a cone of cloud stuff
                ConeEffect coneEffect = new ConeEffect(plugin.elb.getManager(), player);
                coneEffect.setDynamicOrigin(new DynamicLocation(origin));
                coneEffect.setParticle(ParticleEffect.CLOUD);
                coneEffect.start();
                break;
            case Earth:
                //build a wall and make the earth element great again!
                theGreatWall();
                break;
            case Fire:
                // Play the visual of a fire cone
                ConeEffect fireEffect = new ConeEffect(plugin.elb.getManager(), player);
                fireEffect.setDynamicOrigin(new DynamicLocation(origin));
                fireEffect.setParticle(ParticleEffect.FLAME);
                fireEffect.start();
                break;
            case Water:
                // todo: this thing is ugly af from the users perspective... maybe fix that somehow?
                // play the visual of a huge ass water wave
                WaveEffect waveEffect = new WaveEffect(plugin.elb.getManager());
                waveEffect.setDynamicOrigin(new DynamicLocation(origin));
                waveEffect.start();
                break;
            case None:
                // Play the visual of a red stone ray
                RayEffect noneEffect = new RayEffect(plugin.elb.getManager(),
                        new ElementalWaveNoneDamager(player, firstItem));
                noneEffect.setDynamicOrigin(new DynamicLocation(origin));
                noneEffect.setPARTICLE(ParticleEffect.REDSTONE);
                noneEffect.start();
                break;
        }
    }

    private void theGreatWall() {
        //create a giant wall that pushes people away and is basically broke as @#&$
        double angle = player.getLocation().getYaw();
        if (angle <= -(45.0/2.0)) angle += 360;
        if (angle >= 360 - (45.0/2.0)) angle -= 360;

        for (int i=0; i<360; i += 45) {
            if (angle >= (i - (45.0/2.0)) && angle <= (i + (45.0/2.0))) {
                //if this is the right way to go:
                if (i % 90 == 0) {
                    straightWall(player.getEyeLocation(), i);
                } else {
                    angledWall(player.getEyeLocation(), i);
                }
                break;
            }
        }
    }
    private void straightWall(final Location origin, final int angle) {
        final Material wallMaterial = Material.BEDROCK;
        final double knockBack = 1.4;
        final int forwardPlacement = 5;

        //angle can be: 0, 90, 180, 270
        final Vector direction;
        switch (angle) {
            case 0:
                direction = new Vector(0,0,1);
                break;
            case 90:
                direction = new Vector(-1, 0, 0);
                break;
            case 180:
                direction = new Vector(0, 0, -1);
                break;
            case 270:
                direction = new Vector(1, 0, 0);
                break;
            default:
                //not sure if this can happen, but better this than something weird happening
                plugin.logger.info("angle calculation gone wrong... - " + angle);
                return;
        }
        final World world = player.getWorld();
        final Vector middleBlock = origin.toVector().clone().add(direction);
        final Material[][] whereTheWallAt = new Material[5][4];
        for (int width = -2; width <= 2; width++) {
            for (int height = 1; height >= -2; height--) {
                Vector block;
                if (direction.getX() == 0) {
                    block = middleBlock.clone().add(new Vector(width, height, 0));
                } else {
                    block = middleBlock.clone().add(new Vector(0, height, width));
                }
                Location location = block.toLocation(world);
                whereTheWallAt[width+2][height+2] = location.getBlock().getType();
                location.getBlock().setType(wallMaterial);
            }
        }

        new BukkitRunnable() {
            private int iteration = 0;
            @Override
            public void run() {
                for (int width = -2; width <= 2; width++) {
                    for (int height = 1; height >= -2; height--) {
                        Vector block;
                        if (direction.getX() == 0) {
                            block = middleBlock.clone().add(new Vector(width, height, 0));
                        } else {
                            block = middleBlock.clone().add(new Vector(0, height, width));
                        }
                        for (int i=0; i<iteration; i++) {
                            block.add(direction);
                        }
                        //remove the last thingy and put right blocks back
                        Location location = block.toLocation(world);
                        location.getBlock().setType(whereTheWallAt[width+2][height+2]);

                        //to next row
                        location.add(direction);

                        whereTheWallAt[width+2][height+2] = location.getBlock().getType();
                        location.getBlock().setType(wallMaterial);

                        //check next locations for entities to push back
                        location.add(direction);

                        for (Entity e : world.getNearbyEntities(location, 2, 2, 2)) {
                            if (location.getBlock().getLocation().equals(e.getLocation().getBlock().getLocation())) {
                                //if some entity is in the block in front of the wall:
                                e.setVelocity(direction.clone().multiply(knockBack));
                            }
                        }
                    }
                }
                if (iteration == forwardPlacement) {
                    cancel();
                }
                iteration++;
            }
        }.runTaskTimer(plugin, 10, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int width = -2; width <= 2; width++) {
                    for (int height = 1; height >= -2; height--) {
                        Vector block;
                        if (direction.getX() == 0) {
                            block = middleBlock.clone().add(new Vector(width, height, 0));
                        } else {
                            block = middleBlock.clone().add(new Vector(0, height, width));
                        }
                        for (int i=0; i<forwardPlacement+1; i++) {
                            block.add(direction);
                        }
                        //remove the last thingy and put right blocks back
                        Location location = block.toLocation(world);
                        location.getBlock().setType(whereTheWallAt[width+2][height+2]);
                    }
                }
            }
        }.runTaskLater(plugin, 100);



    }
    private void angledWall(final Location origin, final int angle) {
        final Material wallMaterial = Material.BEDROCK;
        final double knockBack = 1.4;
        final int forwardPlacement = 5;

        //angle can be: 45, 135, 225, 315
        final Vector direction;
        switch (angle) {
            case 45:
                direction = new Vector(-1,0,1);
                break;
            case 135:
                direction = new Vector(-1, 0, -1);
                break;
            case 225:
                direction = new Vector(1, 0, -1);
                break;
            case 315:
                direction = new Vector(1, 0, 1);
                break;
            default:
                //again not sure if this can happen, but better this than something weird happening
                plugin.logger.info("angle calculation gone wrong... - " + angle);
                return;
        }
        final World world = player.getWorld();
        final Vector middleBlock = origin.toVector().clone().add(direction);
        final Material[][] whereTheWallAt = new Material[5][4];
        for (int width = -2; width <= 2; width++) {
            for (int height = 1; height >= -2; height--) {

                Vector block;
                if (angle == 45 || angle == 225) {
                    block = middleBlock.clone().add(new Vector(width, height, width));
                } else {
                    block = middleBlock.clone().add(new Vector(width, height, -1*width));
                }


                Location location = block.toLocation(world);
                whereTheWallAt[width+2][height+2] = location.getBlock().getType();
                location.getBlock().setType(wallMaterial);
            }
        }






        new BukkitRunnable() {
            private int iteration = 0;
            @Override
            public void run() {
                for (int width = -2; width <= 2; width++) {
                    for (int height = 1; height >= -2; height--) {

                        Vector block;
                        if (angle == 45 || angle == 225) {
                            block = middleBlock.clone().add(new Vector(width, height, width));
                        } else {
                            block = middleBlock.clone().add(new Vector(width, height, -1*width));
                        }


                        for (int i=0; i<iteration; i++) {
                            block.add(direction);
                        }
                        //remove the last thingy and put right blocks back
                        Location location = block.toLocation(world);
                        location.getBlock().setType(whereTheWallAt[width+2][height+2]);

                        //to next row
                        location.add(direction);

                        whereTheWallAt[width+2][height+2] = location.getBlock().getType();
                        location.getBlock().setType(wallMaterial);

                        //check next locations for entities to push back
                        location.add(direction);

                        for (Entity e : world.getNearbyEntities(location, 2, 2, 2)) {
                            if (location.getBlock().getLocation().equals(e.getLocation().getBlock().getLocation())) {
                                //if some entity is in the block in front of the wall:
                                e.setVelocity(direction.clone().multiply(knockBack));
                            }
                        }
                    }
                }
                if (iteration == forwardPlacement) {
                    cancel();
                }
                iteration++;
            }
        }.runTaskTimer(plugin, 10, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int width = -2; width <= 2; width++) {
                    for (int height = 1; height >= -2; height--) {

                        Vector block;
                        if (angle == 45 || angle == 225) {
                            block = middleBlock.clone().add(new Vector(width, height, width));
                        } else {
                            block = middleBlock.clone().add(new Vector(width, height, -1*width));
                        }


                        for (int i=0; i<forwardPlacement+1; i++) {
                            block.add(direction);
                        }
                        //remove the last thingy and put right blocks back
                        Location location = block.toLocation(world);
                        location.getBlock().setType(whereTheWallAt[width+2][height+2]);
                    }
                }
            }
        }.runTaskLater(plugin, 100);
    }
}
