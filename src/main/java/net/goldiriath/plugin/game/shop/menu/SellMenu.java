package net.goldiriath.plugin.game.shop.menu;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.shop.Product;
import net.goldiriath.plugin.game.shop.ShopProfile;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class SellMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private static final int SIZE = 6 * 9; // 6 rows
    //
    private final ShopProfile profile;
    private final Runnable callback;
    private final SortedMap<Product, Integer> transaction = new TreeMap<>();

    public SellMenu(Goldiriath plugin, ShopProfile profile, Runnable callback) {
        super(plugin);
        this.profile = profile;
        this.callback = callback;
    }

    public static void openMenu(Goldiriath plugin, ShopProfile profile, Player player, Runnable callback) {
        int money = plugin.pym.getData(player).getMoney();
        SellMenu handler = new SellMenu(plugin, profile, callback);
        IconMenu menu = new IconMenu("Sell items - Wallet: " + money + "Pm", SIZE, handler, plugin);

        int slot = 0;
        for (Product product : profile.getSellProducts()) {
            menu.setOption(slot, product.getDisplayStack(), product.toString());
            slot++;
        }

        // Exit button
        menu.setOption(SIZE - 10, StaticItem.MENU_DONE.getStack(), "done");

        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        Player player = event.getPlayer();

        if (event.getName().equals("done")) {
            event.setWillClose(true);
            event.setWillDestroy(true);

            // Add money
            PlayerData data = plugin.pym.getData(player);
            int amt = (int) (TransactionUtil.transactionWorth(transaction) * profile.getExchange());
            data.setMoney(data.getMoney() + amt);

            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1f, 0.9f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1f, 1.3f);

            if (callback != null) {
                callback.run();
            }
            return;
        }

        InventoryClickEvent iClick = event.getEvent();
        int slot = iClick.getSlot();
        int barIndex = slot - (SIZE - 9);
        if (barIndex >= 0 && barIndex < transaction.size()) {
            // Clicked on the transaction bar, remove one of that item

            int transIndex = slot - (SIZE - 9);
            Product product = new ArrayList<>(transaction.keySet()).get(transIndex);
            int amt = transaction.get(product);
            amt--;

            if (amt <= 0) {
                transaction.remove(product);
            } else {
                transaction.put(product, amt);
            }
            TransactionUtil.updateTransaction(iClick.getInventory(), SIZE - 9, transaction);

            // Give the player the item back
            InventoryUtil.storeItem(player.getInventory(), product.getStack(), true);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.8f);
            return;
        }

        // Check if we clicked the player inventory
        if (iClick.getSlot() == iClick.getRawSlot()) {
            // Raw slot == slot: we must be in the top inventory, return
            return;
        }

        ItemStack clickStack = iClick.getCurrentItem();
        if (clickStack == null) {
            // Clicked on an empty spot
            return;
        }

        // Find the matching product
        Product product = null;
        for (Product sellProduct : profile.getSellProducts()) {
            if (sellProduct.getStack().isSimilar(clickStack)) {
                product = sellProduct;
                break;
            }
        }
        if (product == null) {
            // Can't sell this product
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
            return;
        }

        // Remove one item
        if (clickStack.getAmount() == 0) {
            iClick.getInventory().setItem(iClick.getRawSlot(), null);
        } else {
            clickStack.setAmount(clickStack.getAmount() - 1);
        }

        // Add one to the cart
        int amt = transaction.getOrDefault(product, 0);
        amt++;
        transaction.put(product, amt);
        TransactionUtil.updateTransaction(event.getEvent().getInventory(), SIZE - 9, transaction);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
