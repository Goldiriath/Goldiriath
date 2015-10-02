package net.goldiriath.plugin.item;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.item.meta.GItemMeta;
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
                PacketType.Play.Server.WINDOW_ITEMS,
                PacketType.Play.Server.PLAYER_INFO);
        this.gold = plugin;
        this.manager = manager;
    }

    private ItemStack prepItemStack(ItemStack stack) {


        ItemMeta meta = stack.getItemMeta();
        if (meta.getLore() == null || meta.getLore().isEmpty()) {
            return stack;
        }

        GItemMeta gMeta = gold.im.getMeta(stack, false);
        if (gMeta == null) {
            return stack;
        }

        meta.setLore(gMeta.getLore());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        try {
            final PacketContainer packet = event.getPacket();

            // Fix lore on SET_SLOT
            if (event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)) {
                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    return;
                }

                ItemStack stack = packet.getItemModifier().read(0);
                prepItemStack(stack);
                return;
            }

            // Fix lore on WINDOW_ITEMS
            if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                ItemStack[] stacks = packet.getItemArrayModifier().read(0);

                for (ItemStack stack : stacks) {
                    prepItemStack(stack);
                }

                if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    return;
                }
                return;
            }

            // Update inventory when switching gamemodes
            if (event.getPacketType().equals(PacketType.Play.Server.PLAYER_INFO)) {
                event.getPlayer().updateInventory();
            }

        } catch (Exception e) {
        }
    }

}
