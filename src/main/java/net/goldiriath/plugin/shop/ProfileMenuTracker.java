
package net.goldiriath.plugin.shop;

import java.util.Arrays;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.inventory.InventoryUtil;
import net.goldiriath.plugin.player.PlayerData;
import static net.goldiriath.plugin.shop.ShopProfile.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thirdparty.nisovin.iconmenu.OptionClickEvent;
import thirdparty.nisovin.iconmenu.OptionClickEventHandler;
import thirdparty.nisovin.iconmenu.OptionMenu;

public class ProfileMenuTracker implements OptionClickEventHandler {

    public static ItemStack EXIT_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
    public static ItemStack PRODUCT_PLACEHOLDER_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
    public static ItemStack SHOP_BUY_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12);
    public static ItemStack SHOP_SELL_ITEM = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11);
    @Getter
    private final Goldiriath plugin;
    @Getter
    private final ShopProfile profile;
    //
    private OptionMenu chooseMenu;
    private OptionMenu buyMenu;
    private OptionMenu sellMenu;

    public ProfileMenuTracker(ShopProfile profile) {
        this.plugin = profile.getPlugin();
        this.profile = profile;
    }

    public void open(Player player) {
        if (chooseMenu == null) {
            bake();
        }

        chooseMenu.open(player);
    }

    public void openBuy(Player player) {
        buyMenu.open(player);
    }

    public void openSell(Player player) {
        sellMenu.open(player);
    }

    @Override
    public void onOptionClick(final OptionClickEvent event) {
        String name = event.getOption().getName();

        // Exit
        if (name.equals("exit")) {
            event.setClose(true);
            return;
        }

        // Choose: buy
        if (name.equals("buy")) {
            event.setClose(true);

            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    openBuy(event.getPlayer());
                }
            });

            return;
        }

        // Choose: sell
        if (name.equals("sell")) {
            event.setClose(true);

            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    openSell(event.getPlayer());
                }
            });

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
            iClickEvent.setResult(Event.Result.DENY);
            return;
        }

        // Click transaction placeholders
        if (name.equals("transaction")) {
            iClickEvent.setResult(Event.Result.DENY);
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

    public void bake() {
        //
        // Bake choose menu
        //
        chooseMenu = new OptionMenu(plugin, "Shop", 9, this);
        chooseMenu
                .option(position, PRODUCT_PLACEHOLDER_ITEM, null)


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
        optionMenu.option(rows - 9, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 8, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 7, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 6, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 5, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 4, PRODUCT_PLACEHOLDER_ITEM, "transaction");
        optionMenu.option(rows - 3, PRODUCT_PLACEHOLDER_ITEM, "transaction");

        // Cancel button
        optionMenu.option(rows - 1, EXIT_ITEM, "exit");

        return optionMenu;
    }




}
