package net.goldiriath.plugin.shop;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.goldiriath.plugin.TexturedItem;
import net.goldiriath.plugin.inventory.InventoryUtil;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.InfoShop;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thirdparty.nisovin.iconmenu.OptionClickEvent;
import thirdparty.nisovin.iconmenu.OptionClickEventHandler;
import thirdparty.nisovin.iconmenu.OptionMenu;

public class ProfileMenuManager extends AbstractService implements OptionClickEventHandler {

    @Getter
    private final ShopProfile profile;
    //
    private OptionMenu chooseMenu;
    private OptionMenu buyMenu;
    private OptionMenu sellMenu;

    public ProfileMenuManager(ShopProfile profile) {
        super(profile.getPlugin());
        this.profile = profile;
    }

    @Override
    protected void onStart() {
        List<Product> products = profile.getProducts();

        //
        // Bake choose menu
        //
        chooseMenu = new OptionMenu(plugin, "Shop", 9, this);
        chooseMenu
                .option(0, TexturedItem.GUI_SHOP_BUY.getItem(), "buy")
                .option(1, TexturedItem.GUI_SHOP_SELL.getItem(), "sell")
                .option(8, TexturedItem.GUI_EXIT.getItem(), "exit");

        // Calculate size of buy and sell menus
        int rows = (int) Math.ceil((double) products.size() / 9.0) + 1;
        int menuSize = rows * 9;

        //
        // Bake buy menu
        //
        buyMenu = new OptionMenu(plugin, "Buy Items", menuSize, this);

        int slot = 0;
        for (Product product : products) {
            if (product.getAction() == ProductAction.SELL) {
                continue;
            }

            buyMenu.option(slot, product.getDisplayStack(), product.toString());
            slot++;
        }

        // Exit button
        buyMenu.option(menuSize - 1, TexturedItem.GUI_EXIT.getItem(), "exit");

        //
        // Bake sell menu
        //
        sellMenu = new OptionMenu(plugin, "Sell Items", menuSize, this);

        slot = 0;
        for (Product product : products) {
            if (product.getAction() == ProductAction.BUY) {
                continue;
            }

            sellMenu.option(slot, product.getDisplayStack(), product.toString());
            slot++;
        }
    }

    @Override
    protected void onStop() {
        chooseMenu.destroy();
        buyMenu.destroy();
        sellMenu.destroy();
    }

    @Override
    public void onOptionClick(final OptionClickEvent event) {
        final String name = event.hasOption() ? event.getOption().getName() : null;
        final InventoryClickEvent clickEvent = event.getEvent();
        final OptionMenu menu = event.getMenu();

        // Exit
        if ("exit".equals(name)) {
            event.setClose(true);
            return;
        }

        // Default operation
        event.setClose(false);

        if (menu.equals(chooseMenu)) {
            if ("buy".equals(name)) {
                event.setClose(true);

                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        openBuy(event.getPlayer());
                    }
                });

                return;
            }

            if ("sell".equals(name)) {
                event.setClose(true);

                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        openSell(event.getPlayer());
                    }
                });

                return;
            }
        } else if (menu.equals(buyMenu)) { // Player buys items
            event.setClose(false);

            if (event.getEvent().getCurrentItem().equals(TexturedItem.GUI_SHOP_BUY.getItem())) {
                event.setClose(true);
                // TODO: Transact

                return;
            }

            if (name == null) { // Player is clicking the cart / unknown product
                return;
            }

            // Find the right product
            Product product = null;
            for (Product loopProduct : profile.getProducts()) {
                if (loopProduct.toString().equals(name)) {
                    product = loopProduct;
                    break;
                }
            }
            if (product == null) {
                logger.warning("Unknown product at shop: " + profile.getId() + ": " + name);
                return;
            }

            // Find amount to add to or subtract from the cart
            int amount = ShopUtil.amount(event);
            if (amount == 0) {
                return;
            }

            final Player player = event.getPlayer();
            final PlayerData data = plugin.pm.getData(player);
            final Transaction transaction = data.getShop().getTransaction();

            // Update cart
            transaction.updateCart(product, amount);

            // Validate cart
            if (transaction.getTotalPrice() > data.getMoney()) {
                transaction.updateCart(product, -amount);
                player.sendMessage(ChatColor.RED + "You don't have enough money for that!");
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                return;
            }
            if (!transaction.canDisplay()) {
                transaction.updateCart(product, -amount);
                player.sendMessage(ChatColor.RED + "You can't buy more items!");
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
            }

            // Update inventory view
            transaction.display(clickEvent.getView().getBottomInventory());

            // Update exit/buy button
            Inventory shopInventory = clickEvent.getView().getTopInventory();
            if (transaction.hasProducts()) {
                int index = shopInventory.first(TexturedItem.GUI_EXIT.getItem());
                if (index != -1) {
                    shopInventory.setItem(index, TexturedItem.GUI_SHOP_BUY.getItem());
                }
            } else {
                int index = shopInventory.first(TexturedItem.GUI_SHOP_BUY.getItem());
                if (index != -1) {
                    shopInventory.setItem(index, TexturedItem.GUI_EXIT.getItem());
                }
            }

        } else if (menu.equals(sellMenu)) { // Player sells items
            if (name == null) { // Click bottom inventory
                return;
            }

            // Find the right product
            Product product = null;
            for (Product loopProduct : profile.getProducts()) {
                if (loopProduct.toString().equals(name)) {
                    product = loopProduct;
                    break;
                }
            }
            if (product == null) {
                logger.warning("Unknown product at shop: " + profile.getId() + ": " + name);
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

        // Unknown menu
        event.setClose(false);
        logger.warning("Player " + event.getPlayer().getName() + " clicked unknown icon menu: " + name);
    }

    public void open(Player player) {
        final InfoShop info = plugin.pm.getData(player).getShop();
        info.setCurrentMenu(chooseMenu);
        info.setTransaction(null);
        chooseMenu.open(player);
    }

    public void openBuy(Player player) {
        final InfoShop info = plugin.pm.getData(player).getShop();
        info.setCurrentMenu(buyMenu);
        info.setTransaction(new Transaction());
        buyMenu.open(player, false, Bukkit.createInventory(null, 9, "Cart"));
    }

    public void openSell(Player player) {
        final InfoShop info = plugin.pm.getData(player).getShop();
        info.setCurrentMenu(sellMenu);
        info.setTransaction(new Transaction());

        Inventory sellable = Bukkit.createInventory(null, 9, "Sellable items");


        sellMenu.open(player, true, sellable);
    }



}
