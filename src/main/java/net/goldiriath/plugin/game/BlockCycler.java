package net.goldiriath.plugin.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.ConfigPath;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockCycler extends AbstractService {

    private static final List<Material> FORBIDDEN_MATERIALS = Arrays.asList(
            Material.SUGAR_CANE
    );
    //
    private Material tool;
    private final Map<UUID, Biome> biomeMap = new HashMap<>();

    public BlockCycler(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    public void onStart() {
        if (!plugin.config.getBoolean(ConfigPath.CYCLER_ENABLED)) {
            return;
        }

        tool = Util.parseMaterial(plugin.config.getString(ConfigPath.CYCLER_TOOL));

        if (tool == null) {
            plugin.logger.warning("Could not parse cycler tool: " + plugin.config.getString(ConfigPath.CYCLER_TOOL));
        }
    }

    @Override
    public void onStop() {
        biomeMap.clear();
    }

    private boolean validate(PlayerInteractEvent event, boolean sneak, Action action, Material tool) {
        if (event.getAction() != action) {
            return false;
        }

        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            return false;
        }

        if (player.isSneaking() != sneak) {
            return false;
        }

        if (!player.hasPermission("goldiriath.meta")) { // TODO: Better perms
            return false;
        }

        if (!event.hasItem()) {
            return false;
        }

        if (event.getItem().getType() != tool) {
            return false;
        }

        if (FORBIDDEN_MATERIALS.contains(event.getClickedBlock().getType())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            event.setCancelled(true);
            return false;
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    private void setDisplay(ItemStack item, Block block) {
        setDisplay(item, block.getType().toString() + ":" + block.getData() + " (" + block.getBiome().name() + ")");
    }

    private void setDisplay(ItemStack item, String display) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + display);
        item.setItemMeta(meta);
    }

    private void playTick(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1f, 1f);
    }

    @EventHandler
    public void onBiomeCycleRight(PlayerInteractEvent event) {
        if (!validate(event, false, Action.RIGHT_CLICK_BLOCK, tool)) {
            return;
        }

        event.setCancelled(true);

        final Block block = event.getClickedBlock();
        final Biome[] biomes = Biome.values();
        int curIndex = 0;
        for (Biome biome : biomes) {
            if (block.getBiome().equals(biome)) {
                break;
            }
            curIndex++;
        }

        block.setBiome(biomes[(curIndex + 1) % biomes.length]);
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onBiomeCycleLeft(PlayerInteractEvent event) {
        if (!validate(event, false, Action.LEFT_CLICK_BLOCK, tool)) {
            return;
        }

        event.setCancelled(true);

        final Block block = event.getClickedBlock();
        final Biome[] biomes = Biome.values();
        int curIndex = 0;
        for (Biome biome : biomes) {
            if (block.getBiome().equals(biome)) {
                break;
            }
            curIndex++;
        }

        block.setBiome(biomes[curIndex == 0 ? biomes.length - 1 : curIndex - 1]);
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onBiomeCycleCopy(PlayerInteractEvent event) {
        if (!validate(event, true, Action.LEFT_CLICK_BLOCK, tool)) {
            return;
        }

        event.setCancelled(true);

        final Block block = event.getClickedBlock();
        biomeMap.put(event.getPlayer().getUniqueId(), block.getBiome());
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onBiomeCyclePaste(PlayerInteractEvent event) {
        if (!validate(event, true, Action.RIGHT_CLICK_BLOCK, tool)) {
            return;
        }
        event.setCancelled(true);

        final Biome biome = biomeMap.get(event.getPlayer().getUniqueId());
        if (biome == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have any biome set!");
            return;
        }
        final Block block = event.getClickedBlock();
        block.setBiome(biome);
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onPaintingCycleRight(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getItemInHand() == null
                || event.getPlayer().getItemInHand().getType() != tool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) {
            return;
        }

        if (!(event.getRightClicked() instanceof Painting)) {
            return;
        }

        event.setCancelled(true);

        final Painting painting = (Painting) event.getRightClicked();
        final Art[] arts = Art.values();
        int curIndex = 0;
        for (Art art : arts) {
            if (painting.getArt().equals(art)) {
                break;
            }
            curIndex++;
        }

        painting.setArt(arts[(curIndex + 1) % arts.length]);
        setDisplay(event.getPlayer().getItemInHand(), painting.getArt().name());
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onPaintingCycleLeft(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getRemover();

        if (player.getItemInHand() == null
                || player.getItemInHand().getType() != tool) {
            return;
        }

        if (!player.hasPermission("goldiriath.meta")) {
            return;
        }

        if (!(event.getEntity() instanceof Painting)) {
            return;
        }

        event.setCancelled(true);

        final Painting painting = (Painting) event.getEntity();
        final Art[] arts = Art.values();
        int curIndex = 0;
        for (Art art : arts) {
            if (painting.getArt().equals(art)) {
                break;
            }
            curIndex++;
        }

        painting.setArt(arts[curIndex == 0 ? arts.length - 1 : curIndex - 1]);
        setDisplay(player.getItemInHand(), painting.getArt().name());
        playTick(player);
    }

    @EventHandler
    public void updateDisplay(PlayerMoveEvent event) {

        final Player player = event.getPlayer();

        if (player.getItemInHand() == null
                || player.getItemInHand().getType() != tool) {
            return;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        if (!player.hasPermission("goldiriath.meta")) {
            return;
        }

        try {
            Block targetBlock = event.getPlayer().getTargetBlock((HashSet<Material>) null, 50);

            if (targetBlock == null) {
                setDisplay(player.getItemInHand(), "");
                return;
            }

            setDisplay(player.getItemInHand(), targetBlock);
        } catch (Exception ignored) {
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        biomeMap.remove(event.getPlayer().getUniqueId());
    }

}
