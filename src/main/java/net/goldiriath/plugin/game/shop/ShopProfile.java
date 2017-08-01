package net.goldiriath.plugin.game.shop;

import java.util.List;
import java.util.logging.Logger;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.shop.menu.ChooseMenu;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ShopProfile implements ConfigLoadable, Validatable {

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final Logger logger;
    @Getter
    private final String id;
    //
    @Getter
    private String name;
    @Getter
    private double exchange = 0.8;
    @Getter
    private ProductAction type = ProductAction.BOTH;
    @Getter
    private final List<Product> buyProducts = Lists.newArrayList();
    @Getter
    private final List<Product> sellProducts = Lists.newArrayList();

    public ShopProfile(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
    }

    public void openMenu(Player player) {
        openMenu(player, null);
    }

    public void openMenu(Player player, Runnable callback) {
        // TODO: record somewhere, and destroy when shutting down
        ChooseMenu.openMenu(plugin, this, player, callback);
    }

    @Override
    public boolean isValid() {
        return id != null && name != null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = Lists.newArrayListWithExpectedSize(buyProducts.size() + sellProducts.size());
        products.addAll(buyProducts);
        products.addAll(sellProducts);
        return products;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        name = config.getString("name", "Store");

        exchange = config.getDouble("exchange", exchange);

        String typeString = config.getString("type");
        try {
            type = ProductAction.valueOf(typeString);
        } catch (IllegalArgumentException ex) {
            logger.warning("Invalid type in shop " + id + ": " + typeString);
            type = ProductAction.BUY;
        }

        // Products
        buyProducts.clear();
        sellProducts.clear();
        for (String productString : config.getStringList("products")) {
            Product product = Product.load(this, productString);
            if (product == null) {
                continue;
            }

            switch (product.getAction()) {
                case BUY: {
                    buyProducts.add(product);
                    break;
                }

                case SELL: {
                    sellProducts.add(product);
                    break;
                }

                case BOTH: {
                    buyProducts.add(product);
                    sellProducts.add(product);
                    break;
                }

                default: {
                    throw new AssertionError("Action not implemented");
                }
            }
        }
    }

}
