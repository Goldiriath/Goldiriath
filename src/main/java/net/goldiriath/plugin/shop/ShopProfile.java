package net.goldiriath.plugin.shop;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.configuration.ConfigurationSection;

public class ShopProfile implements ConfigLoadable, Validatable {

    @Getter
    private final Goldiriath plugin;
    @Getter
    private final Logger logger;
    @Getter
    private final String id;
    @Getter
    private final ProfileMenuManager menuManager;
    //
    @Getter
    private double exchange = 0.8;
    @Getter
    private ProductAction type = ProductAction.BOTH;
    @Getter
    private final List<Product> products = Lists.newArrayList();

    public ShopProfile(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
        this.menuManager = new ProfileMenuManager(this);
    }

    @Override
    public boolean isValid() {
        return id != null;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {

        menuManager.stop();

        exchange = config.getDouble("exchange", exchange);

        String typeString = config.getString("type");
        try {
            type = ProductAction.valueOf(typeString);
        } catch (IllegalArgumentException ex) {
            logger.warning("Invalid type in shop " + id + ": " + typeString);
            type = ProductAction.BOTH;
        }

        // Products
        products.clear();
        for (String productString : config.getStringList("products")) {
            Product product = Product.load(this, productString);
            if (product != null) {
                products.add(product);
            }
        }

        // Rebake menus for updated products list
        menuManager.start();
    }

}
