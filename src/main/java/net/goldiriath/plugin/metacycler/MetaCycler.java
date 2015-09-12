package net.goldiriath.plugin.metacycler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.goldiriath.plugin.ConfigPaths;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Art;
import org.bukkit.ChatColor;
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

public class MetaCycler extends AbstractService {

    private Material metaTool;
    private Material biomeTool;
    private final Map<UUID, Byte> dataMap = new HashMap<>();
    private final Map<UUID, Biome> biomeMap = new HashMap<>();

    public MetaCycler(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    public void onStart() {
        if (!plugin.config.getBoolean(ConfigPaths.METACYCLER_ENABLED)) {
            return;
        }

        metaTool = Util.parseMaterial(plugin.config.getString(ConfigPaths.METACYCLER_META_TOOL));
        biomeTool = Util.parseMaterial(plugin.config.getString(ConfigPaths.METACYCLER_BIOME_TOOL));

        if (metaTool == null) {
            plugin.logger.warning("Could not parse MetaCycler meta tool: " + plugin.config.getString(ConfigPaths.METACYCLER_META_TOOL));
        }
        if (biomeTool == null) {
            plugin.logger.warning("Could not parse MetaCycler biome tool: " + plugin.config.getString(ConfigPaths.METACYCLER_BIOME_TOOL));
        }
    }

    @Override
    public void onStop() {
        dataMap.clear();
        biomeMap.clear();
    }

    @SuppressWarnings("deprecation")
    private void setDisplay(ItemStack item, Block block) {
        setDisplay(item, block.getTypeId() + ":" + block.getData() + " (" + block.getBiome().name() + ")");
    }

    private void setDisplay(ItemStack item, String display) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + display);
        item.setItemMeta(meta);
    }

    private void playTick(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onDataCycleRight(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != metaTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock().getType() == Material.DOUBLE_PLANT) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            return;
        }

        final Block block = event.getClickedBlock();
        block.setData((byte) ((block.getData() + 1) % 16));
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onDataCycleLeft(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != metaTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock().getType() == Material.DOUBLE_PLANT) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            return;
        }

        final Block block = event.getClickedBlock();
        final byte newData = (byte) (block.getData() - 1);
        block.setData(newData < 0 ? 15 : newData);
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onDataCycleCopy(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != metaTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock().getType() == Material.DOUBLE_PLANT) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            return;
        }

        final Block block = event.getClickedBlock();
        dataMap.put(event.getPlayer().getUniqueId(), block.getData());
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onDataCyclePaste(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != metaTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        event.setCancelled(true);

        if (event.getClickedBlock().getType() == Material.DOUBLE_PLANT) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            return;
        }

        final Byte data = dataMap.get(event.getPlayer().getUniqueId());
        if (data == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have any block data set!");
            return;
        }
        final Block block = event.getClickedBlock();
        block.setData(data);
        setDisplay(event.getItem(), block);
        playTick(event.getPlayer());
    }

    @EventHandler
    public void onBiomeCycleRight(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != biomeTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
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
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != biomeTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
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
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != biomeTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if (!event.hasItem()) {
            return;
        }

        if (event.getItem().getType() != biomeTool) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
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
                || event.getPlayer().getItemInHand().getType() != metaTool) {
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
                || player.getItemInHand().getType() != metaTool) {
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
                || (player.getItemInHand().getType() != metaTool
                && player.getItemInHand().getType() != biomeTool)) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) {
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
        dataMap.remove(event.getPlayer().getUniqueId());
        biomeMap.remove(event.getPlayer().getUniqueId());
    }

}
