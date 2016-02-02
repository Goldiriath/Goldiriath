package net.goldiriath.plugin;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.service.AbstractService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AutoClose extends AbstractService {

    public static final long AUTO_CLOSE_TICKS = 100L;
    //
    private final Map<Location, AutoCloseEntry> entries = new HashMap<>();

    public AutoClose(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        entries.clear();
    }

    @Override
    protected void onStop() {
        for (AutoCloseEntry entry : entries.values()) {
            entry.cancel();
            entry.doAction();
        }

        entries.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockRightClick(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }

        final BlockState blockState = getOpenableBlockState(event.getClickedBlock());
        if (blockState == null) {
            return;
        }

        final Location loc = blockState.getLocation();
        final Player player = event.getPlayer();
        final Openable openable = (Openable) blockState.getData();

        AutoCloseEntry oldEntry = entries.get(loc);
        if (oldEntry != null) {
            oldEntry.cancel();
            entries.remove(loc);
        }

        // If the player is in creative, cancel the entry if it's present and thus set its default state
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(ChatColor.GREEN + "Set block autoclose state to: " + !openable.isOpen());
            return;
        }

        // Don't make a new entry if the player is prematurely setting the openable to the default state
        if (oldEntry != null
                && !oldEntry.isFinished()
                && oldEntry.isDefaultOpen() != openable.isOpen()) {
            return;
        }

        // Make and start a new reset entry
        final AutoCloseEntry newEntry = new AutoCloseEntry(blockState, openable.isOpen());
        newEntry.start();
        entries.put(loc, newEntry);
    }

    private BlockState getOpenableBlockState(Block block) {
        return getOpenableBlockState(block, false);
    }

    // TODO: Document what witchcraft is going on here...
    @SuppressWarnings("deprecation")
    private BlockState getOpenableBlockState(Block block, boolean failQuick) {
        if (block == null) {
            return null;
        }

        final MaterialData data = block.getState().getData();

        if (!(data instanceof Openable)) {
            return null;
        }

        if (!(data instanceof Door)) {
            return block.getState();
        }

        if (((Door) data).isTopHalf()) {
            if (failQuick) {
                plugin.logger.warning("AutoClose: Could not obtain Openable from block! (failQuick)");
                return null;
            }

            return getOpenableBlockState(block.getRelative(BlockFace.DOWN), true);
        }

        return ((Door) data).isTopHalf() ? null : block.getState();
    }

    public class AutoCloseEntry {

        @Getter
        private final BlockState state;
        @Getter
        private final boolean defaultOpen;
        //
        private BukkitTask task;
        @Getter
        private Openable openable;
        @Getter
        private boolean finished;

        public AutoCloseEntry(BlockState state, boolean defaultOpen) {
            this.state = state;
            try {
                this.openable = (Openable) state.getData();
            } catch (ClassCastException ex) {
                Goldiriath.plugin.logger.warning("AutoClose: BlockState is not castable to be cast to Openable!");
            }
            this.defaultOpen = defaultOpen;
            this.finished = false;
        }

        public void doAction() {
            if (openable == null) {
                return;
            }
            openable.setOpen(defaultOpen);
            state.update(true);

            final Sound sound = defaultOpen ? Sound.DOOR_OPEN : Sound.DOOR_CLOSE;
            state.getLocation().getWorld().playSound(state.getLocation(), sound, 1f, 1f);
        }

        public void cancel() {
            if (task == null) {
                return;
            }

            try {
                task.cancel();
            } catch (Exception ex) {
                plugin.logger.warning("AutoClose: Could not cancel task!");
                plugin.logger.warning(ExceptionUtils.getFullStackTrace(ex));
            } finally {
                task = null;
            }
        }

        public void start() {
            cancel();
            task = new BukkitRunnable() {

                @Override
                public void run() {
                    finished = true;
                    doAction();
                }
            }.runTaskLater(plugin, AUTO_CLOSE_TICKS);
        }

    }

}
