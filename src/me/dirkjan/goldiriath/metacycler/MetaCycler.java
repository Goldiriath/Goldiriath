package me.dirkjan.goldiriath.metacycler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.listener.RegistrableListener;
import me.dirkjan.goldiriath.util.Service;
import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerQuitEvent;

public class MetaCycler extends RegistrableListener implements Service {

    private final Map<UUID, Byte> dataMap = new HashMap<>();
    private final Map<UUID, Biome> biomeMap = new HashMap<>();

    public MetaCycler(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        register();
    }

    @Override
    public void stop() {
        dataMap.clear();
        biomeMap.clear();
        unregister();
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

        if (event.getItem().getType() != Material.STICK) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        if (event.getClickedBlock().getType() == Material.DOUBLE_PLANT) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot edit this block's meta data.");
            return;
        }

        final Block block = event.getClickedBlock();
        block.setData((byte) ((block.getData() + 1) % 16));
        event.getPlayer().sendMessage(ChatColor.GREEN + "Set block to " + block.getTypeId() + ":" + block.getData());
        event.setCancelled(true);
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

        if (event.getItem().getType() != Material.STICK) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Set block to " + block.getTypeId() + ":" + block.getData());
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

        if (event.getItem().getType() != Material.STICK) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Copied block data: " + block.getTypeId() + ":" + block.getData());
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

        if (event.getItem().getType() != Material.STICK) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Pasted block data: " + block.getTypeId() + ":" + block.getData());
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

        if (event.getItem().getType() != Material.BLAZE_ROD) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Set block biome to: " + block.getBiome().name());
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

        if (event.getItem().getType() != Material.BLAZE_ROD) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Set block biome to: " + block.getBiome().name());
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

        if (event.getItem().getType() != Material.BLAZE_ROD) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) { // TODO: Better perms
            return;
        }

        event.setCancelled(true);

        final Block block = event.getClickedBlock();
        biomeMap.put(event.getPlayer().getUniqueId(), block.getBiome());
        event.getPlayer().sendMessage(ChatColor.GREEN + "Copied block biome: " + block.getBiome().name());
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

        if (event.getItem().getType() != Material.BLAZE_ROD) {
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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Pasted block biome: " + block.getBiome().name());
    }

    @EventHandler
    public void onPaintingCycleRight(PlayerInteractEntityEvent event) {
        if (event.getPlayer().getItemInHand() == null
                || event.getPlayer().getItemInHand().getType() != Material.STICK) {
            return;
        }

        if (!event.getPlayer().hasPermission("goldiriath.meta")) {
            return;
        }

        if (!(event.getRightClicked() instanceof Painting)) {
            return;
        }

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
        event.getPlayer().sendMessage(ChatColor.GREEN + "Set painting art to: " + painting.getArt().name());
        event.setCancelled(true);
    }

    @EventHandler
    public void onPaintingCycleLeft(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getRemover();

        if (player.getItemInHand() == null
                || player.getItemInHand().getType() != Material.STICK) {
            return;
        }

        if (!player.hasPermission("goldiriath.meta")) {
            return;
        }

        if (!(event.getEntity() instanceof Painting)) {
            return;
        }

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
        player.sendMessage(ChatColor.GREEN + "Set painting art to: " + painting.getArt().name());
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataMap.remove(event.getPlayer().getUniqueId());
        biomeMap.remove(event.getPlayer().getUniqueId());
    }

}
