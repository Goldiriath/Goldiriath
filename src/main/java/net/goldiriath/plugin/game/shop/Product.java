package net.goldiriath.plugin.game.shop;

import java.util.Arrays;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Product implements Comparable<Product> {

    @Getter
    private final ItemStack stack;
    @Getter
    private final int price;
    @Getter
    private final ProductAction action;

    public Product(ItemStack stack, int value, ProductAction action) {
        this.stack = stack;
        this.price = value;
        this.action = action;
    }

    public String getDescription() {
        return stack.getType().toString().toLowerCase().replace('_', ' ');
    }

    public ItemStack getDisplayStack() {

        ItemStack display = getStack().clone();
        ItemMeta meta = display.getItemMeta();

        meta.setLore(Arrays.asList(new String[]{
            ChatColor.GOLD.toString() + ChatColor.ITALIC + getDescription(),
            ChatColor.RED.toString() + price + " Pm"
        }));

        display.setItemMeta(meta);

        return display;
    }

    @Override
    public int compareTo(Product t) {
        return stack.getType().compareTo(t.getStack().getType());
    }

    @Override
    public String toString() {
        return stack.toString() + ":" + price + ":" + action;
    }

    public static Product load(ShopProfile profile, String productString) {
        final String id = profile.getId();
        final Logger logger = profile.getLogger();
        final String[] parts = productString.split(" ");

        if (parts.length != 2 && parts.length != 3) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid format!");
            return null;
        }

        ItemStack stack = Util.parseItem(parts[0]);
        if (stack == null) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid item!");
            return null;
        }
        stack = stack.clone(); // Don't set amounts on ItemManager items
        stack.setAmount(1);

        int price;
        try {
            price = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ignored) {
            logger.warning("Not loading shop product " + id + " - " + productString + ". Invalid price!");
            return null;
        }

        ProductAction action = ProductAction.BOTH;
        if (parts.length == 3 && parts[2] != null) {
            switch (parts[2]) {
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

        return new Product(stack, price, action);
    }

}
