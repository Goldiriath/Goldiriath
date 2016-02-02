package net.goldiriath.plugin.shop;

import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.util.Util;
import org.bukkit.inventory.ItemStack;

public class Product {

    @Getter
    private final int amount;
    @Getter
    private final ItemStack stack;
    @Getter
    private final int price;
    @Getter
    private final ProductAction action;

    public Product(int amount, ItemStack stack, int value) {
        this(amount, stack, value, ProductAction.BUY_OR_SELL);
    }

    public Product(int amount, ItemStack stack, int value, ProductAction action) {
        this.amount = amount;
        this.stack = stack;
        this.price = value;
        this.action = action;
    }

    @Override
    public String toString() {
        return stack.toString() + ":" + amount + ":" + price + ":" + action;
    }

    public String getDescription() {
        return amount + " " + stack.getType().toString().toLowerCase().replace('_', ' ');
    }

    public static Product load(ShopProfile profile, String productString) {
        final String id = profile.getId();
        final Logger logger = profile.getLogger();
        final String[] parts = productString.split(" ");

        if (parts.length != 3 && parts.length != 4) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid format!");
            return null;
        }

        int amount;
        try {
            amount = Integer.parseInt(parts[0]);
        } catch (NumberFormatException ignored) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid amount!");
            return null;
        }

        ItemStack stack = Util.parseItem(parts[1]);
        if (stack == null) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid item!");
            return null;
        }
        stack = stack.clone(); // Don't set amounts on ItemManager items
        stack.setAmount(amount);

        int price;
        try {
            price = Integer.parseInt(parts[2]);
        } catch (NumberFormatException ignored) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid price!");
            return null;
        }

        ProductAction action = ProductAction.BUY_OR_SELL;
        if (parts.length == 4 && parts[3] != null) {
            switch (parts[3]) {
                case "buy":
                    action = ProductAction.BUY;
                    break;
                case "sell":
                    action = ProductAction.SELL;
                    break;
                default:
                    logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid action!");
                    return null;
            }
        }

        return new Product(amount, stack, price, action);
    }

}
