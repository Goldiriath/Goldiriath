package net.goldiriath.plugin.game.shop.menu;

import java.util.Map;
import java.util.SortedMap;
import net.goldiriath.plugin.game.shop.Product;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TransactionUtil {

    private TransactionUtil() {
    }

    public static int transactionWorth(Map<Product, Integer> trans) {
        int amt = 0;
        for (Map.Entry<Product, Integer> entry : trans.entrySet()) {
            amt += entry.getKey().getPrice() * entry.getValue();
        }
        return amt;
    }

    public static void updateTransaction(Inventory inv, int startIndex, SortedMap<Product, Integer> trans) {
        // Clear previous transactions..
        for (int i = startIndex; i < 9; i++) {
            inv.setItem(i, null);
        }

        // Store the items
        int i = startIndex;
        for (Product product : trans.keySet()) {
            ItemStack stack = product.getStack().clone();
            stack.setAmount(trans.get(product));
            inv.setItem(i++, stack);
        }
    }

}
