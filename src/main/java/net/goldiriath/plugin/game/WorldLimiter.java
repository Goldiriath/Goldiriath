package net.goldiriath.plugin.game;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.loot.ChestSpawn;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class WorldLimiter extends AbstractService {

    public static final Tag[] ALLOWED_BLOCK_TAGS = {
        Tag.BUTTONS,
        Tag.TRAPDOORS,
        Tag.WOODEN_PRESSURE_PLATES,
        Tag.DOORS,
    };
    
    public static final Material[] ALLOWED_BLOCK_MATERIALS = {
        Material.LEVER,
        Material.TRIPWIRE,
        Material.TRIPWIRE_HOOK,
        Material.LECTERN,
        Material.CAULDRON,
        Material.CHEST
    };
    
    public static final Material[] ALLOWED_ITEM_MATERIALS = {
        Material.BUCKET,
        Material.WRITTEN_BOOK,
        Material.BOW,
        Material.SHEARS,
        Material.BREAD,
        Material.POTION,
        Material.SPLASH_POTION,
    };
    
    public WorldLimiter(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_BLOCK:
            case PHYSICAL: {
                if (!event.hasBlock()) {
                    return;
                }
                
                Material m = event.getClickedBlock().getType();
                
                for (Tag allowed : ALLOWED_BLOCK_TAGS) {
                    if (allowed.isTagged(m)) {
                        return;
                    }
                }

                for (Material allowed : ALLOWED_BLOCK_MATERIALS) {
                    if (allowed == m) {
                        return;
                    }
                }
                
                // Allow opening of lecterns
                if (m == Material.LECTERN) {
                    // Check that we're not adding a book to the lectern
                    if (!event.hasItem() || !Tag.ITEMS_LECTERN_BOOKS.isTagged(event.getItem().getType())) {
                        return;
                    }
                    break;
                }

                // Default: block interactions not allowed
                event.setCancelled(true);
                break;
            }
            case RIGHT_CLICK_AIR:
            case LEFT_CLICK_AIR: {
                if (!event.hasItem()) {
                    return;
                }
                
                Material m = event.getItem().getType();
                
                if (m.isEdible()) {
                    return; // Allow food
                }
                
                for (Material allowed : ALLOWED_ITEM_MATERIALS) {
                    if (allowed == m) {
                        return;
                    }
                }

                // Default: item interactions not allowed
                event.setCancelled(true);
                break;
            }
            default:
                // By default, interactions are not permitted
                event.setCancelled(true);
        }
    }
    
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        
        // If we're filling water, allow it
        if (event.getBlock().getType() == Material.WATER) {
            
            // Hacky way to restore the water after taking it
            final Location l = event.getBlock().getLocation();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                l.getWorld().getBlockAt(l).setType(Material.WATER);
            }, 1);
            
            return;
        } else if (event.getBucket() == Material.MILK_BUCKET) {
            // Allow filling milk buckets
            return;
        }

        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        HumanEntity entity = event.getPlayer();
        if (entity.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        switch (event.getInventory().getType()) {
            case CHEST:
                // Handled seperately
                if (!(event.getInventory().getHolder() instanceof Chest)) {
                    return;
                }

                Chest chest = (Chest) event.getInventory().getHolder();
                for (ChestSpawn spawn : plugin.ltm.getSpawns()) {
                    if (spawn.getLocation().equals(chest.getLocation())) {
                        return;
                    }
                }
                event.setCancelled(true); // Not a loot chest

            case LECTERN:
                break; // Handled seperately
            case PLAYER:
                break; // Allowed
            default:
                player.sendMessage(ChatColor.RED + "You cannot open this.");
                event.setCancelled(true);
                break;
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerLecternBookTake(PlayerTakeLecternBookEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        // Prevent taking lectern books
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        switch(event.getSpawnReason()) {
            case CUSTOM:
            case DISPENSE_EGG:
            case DEFAULT:
                return; // Allow
            default:
                event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityPortal(EntityPortalEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getCause() != RemoveCause.ENTITY) {
            event.setCancelled(true); // No explosions/water, etc.
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
         if (!(event.getRemover() instanceof Player)) {
             event.setCancelled(true);
             return;
         }
         
         Player p = (Player) event.getRemover();
         
         if (p.getGameMode() != GameMode.SURVIVAL) {
             return;
         }
         
         event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() != null && event.getAttacker() instanceof Player) {
            return; // Allow players to destroy boats/minecarts for now
        }
        
        event.setCancelled(true);
    }

}
