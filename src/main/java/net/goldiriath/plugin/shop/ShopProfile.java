package net.goldiriath.plugin.shop;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.inventory.InventoryUtil;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Registrable;
import net.goldiriath.plugin.util.Util;
import net.goldiriath.plugin.util.Validatable;
import net.pravian.bukkitlib.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thirdparty.menu.OptionClickEvent;
import thirdparty.menu.OptionClickEventHandler;
import thirdparty.menu.OptionMenu;

public class ShopProfile implements ConfigLoadable, Validatable, OptionClickEventHandler {

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
        int size = products.size();
        while (size % 9 != 0) {
            size++;
        }
        if (size < 28) { // At least two rows
            size = 28;
        }

        OptionMenu optionMenu = new OptionMenu(plugin, id, size, this);

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

        optionMenu.option(size - 1, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14), "exit");

        return optionMenu;
    }

    @Override
    public boolean isValid() {
        return id != null;
    }

}
