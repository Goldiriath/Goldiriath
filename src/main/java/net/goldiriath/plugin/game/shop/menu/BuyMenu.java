package net.goldiriath.plugin.game.shop.menu;

import java.util.ArrayList;
import java.util.Map;
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
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BuyMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private static final int SIZE = 3 * 9; // 3 rows
    //
    private final ShopProfile profile;
    private final Runnable callback;
    private final SortedMap<Product, Integer> transaction = new TreeMap<>();

    private BuyMenu(Goldiriath plugin, ShopProfile profile, Runnable callback) {
        super(plugin);
        this.profile = profile;
        this.callback = callback;
    }

    public static void openMenu(Goldiriath plugin, ShopProfile profile, Player player, Runnable callback) {
        int money = plugin.pm.getData(player).getMoney();
        BuyMenu handler = new BuyMenu(plugin, profile, callback);
        IconMenu menu = new IconMenu("Buy items - Wallet: " + money + "Pm", SIZE, handler, plugin);

        int slot = 0;
        for (Product product : profile.getBuyProducts()) {
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
            PlayerInventory inv = player.getInventory();

            // Subtract money, we assume the player has enough money
            // since adding an item to the transaction performs this check
            PlayerData data = plugin.pm.getData(player);
            data.setMoney(data.getMoney() - TransactionUtil.transactionWorth(transaction));

            // Add items
            for (Map.Entry<Product, Integer> entry : transaction.entrySet()) {
                ItemStack stack = entry.getKey().getStack().clone();
                stack.setAmount(entry.getValue());
                InventoryUtil.storeItem(inv, stack, true);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1f, 1.2f);
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
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);

            return;
        }

        // Check if we clicked the player inventory
        if (iClick.getSlot() != iClick.getRawSlot()) {
            // Raw slot != slot: we must be in the bottom inventory, return
            return;
        }

        // Clicked on a product (maybe?), add one of that item
        Product product = null;
        for (Product buyProduct : profile.getBuyProducts()) {
            if (buyProduct.toString().equals(event.getName())) {
                product = buyProduct;
                break;
            }
        }

        if (product == null) {
            return;
        }

        // Check if we have enough money
        int money = plugin.pm.getData(player).getMoney();
        if (TransactionUtil.transactionWorth(transaction) + product.getPrice() > money) {
            player.sendMessage(ChatColor.RED + "The shopkeeper looks at you angrily and says: "
                    + "\"Do you have enough money for that?\"");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
            return;
        }

        // Add one
        int amt = transaction.getOrDefault(product, 0);
        amt++;
        transaction.put(product, amt);
        TransactionUtil.updateTransaction(iClick.getInventory(), SIZE - 9, transaction);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.8f);
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
