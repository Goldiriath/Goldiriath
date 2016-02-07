package net.goldiriath.plugin.shop;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.inventory.InventoryUtil;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thirdparty.menu.OptionClickEvent;
import thirdparty.menu.OptionClickEventHandler;
import thirdparty.menu.OptionMenu;

public class ShopProfile implements ConfigLoadable, Validatable, OptionClickEventHandler {

    public static ItemStack CANCEL_BUTTON = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
    public static ItemStack TRANSACTION = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
    //
    @Getter
    private final Goldiriath plugin;
    @Getter
    private final Logger logger;
    @Getter
    private final String id;
    //
    @Getter
    private double exchange = 0.8;
    @Getter
    private final List<Product> products = Lists.newArrayList();
    private OptionMenu menu;

    public ShopProfile(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
    }

    public void open(Player player) {
        if (menu == null) {
            makeMenu();
        }

        menu.open(player);
    }

    @Override
    public void loadFrom(ConfigurationSection config) {

        exchange = config.getDouble("exchange", exchange);

        // Products
        products.clear();
        for (String productString : config.getStringList("products")) {
            Product product = Product.load(this, productString);
            if (product != null) {
                products.add(product);
            }
        }

        // Recreate menu
        if (menu != null) {
            menu.destroy();
        }
        menu = makeMenu();
    }

    @Override
    public void onOptionClick(OptionClickEvent event) {
        String name = event.getOption().getName();

        // Only close when clicking the close item
        if (name.equals("exit")) {
            event.setClose(true);
            return;
        }
        event.setClose(false);

        // Find the right product
        Product product = null;
        for (Product loopProduct : products) {
            if (loopProduct.toString().equals(name)) {
                product = loopProduct;
            }
        }

        if (product == null) {
            logger.warning("Unknown product at shop: " + id + ": " + name);
            return;
        }

        final Player player = event.getPlayer();
        final PlayerData data = plugin.pm.getData(player);
        final InventoryClickEvent iClickEvent = event.getEvent();

        if (!iClickEvent.isLeftClick()) {
            iClickEvent.setResult(Result.DENY);
            return;
        }

        // Click transaction placeholders
        if (name.equals("transaction")) {
            iClickEvent.setResult(Result.DENY);
            return;
        }

        if (iClickEvent.isLeftClick()) { // Shop sells item
            if (!data.hasMoney(product.getAmount())) {
                player.sendMessage(ChatColor.RED + "You don't have enough money for that!");
                return;
            }

            if (InventoryUtil.storeItem(player.getInventory(), product.getStack(), false)) {
                data.removeMoney(product.getAmount());
                player.sendMessage(ChatColor.GREEN + "You bought " + product.getDescription() + " for " + product.getPrice() + " Pm");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have enough space for that!");
            }

        } else if (iClickEvent.isRightClick()) { // Shop buys item

            if (InventoryUtil.removeItem(player.getInventory(), product.getStack())) {
                data.addMoney(Math.min((int) (product.getPrice() * exchange), product.getPrice()));
                player.sendMessage(ChatColor.GREEN + "You sold " + product.getDescription() + " for " + product.getPrice() + " Pm");
            } else {
                player.sendMessage(ChatColor.RED + "You don't have enough items to sell!");
            }

        }
    }

    private OptionMenu makeMenu() {
        int rows = (int) Math.ceil((double) products.size() / 3.0);

        // One row extra space
        // One row: transaction + buttons
        rows += 2;

        OptionMenu optionMenu = new OptionMenu(plugin, id, rows * 3, this);

        // Fill items
        int slot = 0;
        for (Product product : products) {
            // Bake item with meta
            ItemStack stack = product.getStack().clone();
            ItemMeta meta = stack.getItemMeta();
            meta.setLore(Arrays.asList(new String[]{
                ChatColor.GOLD.toString() + ChatColor.ITALIC + product.getDescription(),
                ChatColor.RED.toString() + product.getPrice() + " Pm"
            }));
            stack.setItemMeta(meta);

            optionMenu.option(slot, product.getStack(), product.toString());
            slot++;
        }

        // Transaction placeholders
        optionMenu.option(rows - 9, TRANSACTION, "transaction");
        optionMenu.option(rows - 8, TRANSACTION, "transaction");
        optionMenu.option(rows - 7, TRANSACTION, "transaction");
        optionMenu.option(rows - 6, TRANSACTION, "transaction");
        optionMenu.option(rows - 5, TRANSACTION, "transaction");
        optionMenu.option(rows - 4, TRANSACTION, "transaction");
        optionMenu.option(rows - 3, TRANSACTION, "transaction");

        // Cancel button
        optionMenu.option(rows - 1, CANCEL_BUTTON, "exit");

        return optionMenu;
    }

    @Override
    public boolean isValid() {
        return id != null;
    }

}
