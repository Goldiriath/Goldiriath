package net.goldiriath.plugin.item;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
import net.pravian.bukkitlib.util.ChatUtils;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProtocolLibAdapter extends PacketAdapter {

    @Getter
    private final Goldiriath gold;
    @Getter
    private final ProtocolManager manager;

    public ProtocolLibAdapter(Goldiriath plugin, ProtocolManager manager) {
        super(plugin, ListenerPriority.HIGH,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS);
        this.gold = plugin;
        this.manager = manager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        plugin.getLogger().fine("ProtocolLib adapter - Sending packet: " + event.getPacketType().name());
        try {
            final PacketContainer packet = event.getPacket();

            // Fix lore on SET_SLOT
            if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    plugin.getLogger().fine("  Not modifying packet: player in creative");
                    return;
                }

                ItemStack stack = packet.getItemModifier().read(0);
                rewriteLore(stack);
            } // Fix lore on WINDOW_ITEMS
            else if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                ItemStack[] stacks = packet.getItemArrayModifier().read(0);

                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    plugin.getLogger().fine("  Not modifying packet: player in creative");
                    return;
                }

                for (ItemStack stack : stacks) {
                    rewriteLore(stack);
                }
            }
        } catch (Exception e) {
        }
    }

    private void rewriteLore(ItemStack stack) {

        final String desc = stack.getAmount() + " of " + stack.getType() + ":" + stack.getData().getData();

        ItemMeta meta = stack.getItemMeta();
        if (!meta.hasLore() || meta.getLore().isEmpty()) {
            plugin.getLogger().fine("  No lore: " + desc);
            return;
        }

        GItemMeta gMeta = gold.im.getMeta(stack, false);
        if (gMeta == null) {
            plugin.getLogger().fine("  No itemmeta: " + desc);
            return;
        }

        plugin.getLogger().fine("  Replacing lore: " + desc);
        List<String> lore = gMeta.getLore();
        if (lore != null && lore.isEmpty()) {
            List<String> newLore = Lists.newArrayList();
            for (String loreString : lore) {
                newLore.add(ChatUtils.colorize(loreString));
            }
            lore = newLore;
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);

        String name = gMeta.getName();
        if (gMeta.getName() != null) {
            plugin.getLogger().fine("  Replacing name: ");
            meta.setDisplayName(ChatUtils.colorize(name));
        }
    }

}
