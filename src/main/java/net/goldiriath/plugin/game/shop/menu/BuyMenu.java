package net.goldiriath.plugin.game.shop.menu;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.inventory.InventoryUtil;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.shop.Product;
import net.goldiriath.plugin.game.shop.ProductAction;
import net.goldiriath.plugin.game.shop.ShopProfile;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BuyMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private static final int SIZE = 6 * 9; // 6 rows
    //
    private final ShopProfile profile;
    private final SortedMap<Product, Integer> transaction = new TreeMap<>();

    private BuyMenu(Goldiriath plugin, ShopProfile profile, Player player) {
        super(plugin);
        this.profile = profile;
    }

    public static void openMenu(Goldiriath plugin, ShopProfile profile, Player player) {
        int money = plugin.pm.getData(player).getMoney();
        BuyMenu handler = new BuyMenu(plugin, profile, player);
        IconMenu menu = new IconMenu("Buy items - Wallet: " + money + "Pm", SIZE, handler, plugin);

        int slot = 0;
        for (Product product : profile.getProducts()) {
            if (product.getAction() == ProductAction.SELL) {
                continue;
            }

            menu.setOption(slot, product.getDisplayStack(), product.toString());
            slot++;
        }

        // Exit button
        menu.setOption(SIZE - 10, StaticItem.MENU_DONE.getStack(), "done");

        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        if (event.getName().equals("done")) {
            event.setWillClose(true);
            event.setWillDestroy(true);

            Player player = event.getPlayer();
            PlayerInventory inv = player.getInventory();

            // Subtract money, we assume the player has enough money
            // since adding an item to the transaction performs this check
            PlayerData data = plugin.pm.getData(player);
            data.setMoney(data.getMoney() - transactionWorth(transaction));

            // Add items
            for (Entry<Product, Integer> entry : transaction.entrySet()) {
                ItemStack stack = entry.getKey().getStack().clone();
                stack.setAmount(entry.getValue());
                InventoryUtil.storeItem(inv, stack, true);
            }

            player.playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1f, 0.9f);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1.3f);
            return;
        }

        int slot = event.getEvent().getSlot();
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
            updateTransaction(event.getEvent().getInventory(), transaction);
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 0.8f);
            return;
        }

        // Clicked on a product (maybe?), add one of that item
        Product product = null;
        for (Product shopProduct : profile.getProducts()) {
            if (shopProduct.toString().equals(event.getName())) {
                product = shopProduct;
                break;
            }
        }

        if (product == null) {
            return;
        }

        // Check if we have enough money
        Player player = event.getPlayer();
        int money = plugin.pm.getData(player).getMoney();
        if (transactionWorth(transaction) + product.getPrice() > money) {
            player.sendMessage(ChatColor.RED + "The shopkeeper looks at you angrily and says: "
                    + "\"Do you have enough money for that?\"");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 1f, 1f);
            return;
        }

        // Add one
        int amt = transaction.getOrDefault(product, 0);
        amt++;
        transaction.put(product, amt);
        updateTransaction(event.getEvent().getInventory(), transaction);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1.2f);
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    private static int transactionWorth(Map<Product, Integer> trans) {
        int amt = 0;
        for (Entry<Product, Integer> entry : trans.entrySet()) {
            amt += entry.getKey().getPrice() * entry.getValue();
        }
        return amt;
    }

    private static void updateTransaction(Inventory inv, SortedMap<Product, Integer> trans) {
        // Clear previous transactions..
        for (int i = SIZE - 9; i < SIZE; i++) {
            inv.setItem(i, null);
        }

        // Store the items
        int i = SIZE - 9;
        for (Product product : trans.keySet()) {
            ItemStack stack = product.getStack().clone();
            stack.setAmount(trans.get(product));
            inv.setItem(i++, stack);
        }
    }

}
