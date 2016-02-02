
package net.goldiriath.plugin.shop;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.PrefixFileFilter;
import net.goldiriath.plugin.util.service.AbstractService;
import net.pravian.bukkitlib.config.YamlConfig;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ShopManager extends AbstractService {

    @Getter
    private final File shopContainer;
    @Getter
    private final Map<String, ShopProfile> profiles = Maps.newHashMap();

    public ShopManager(Goldiriath plugin) {
        super(plugin);
        this.shopContainer = new File(plugin.getDataFolder(), "shops");
    }

    @Override
    protected void onStart() {
        // Ensure folder is present
        if (shopContainer.isFile()) {
            if (!shopContainer.delete()) {
                logger.severe("Not loading shops! Could not delete file: " + shopContainer.getAbsolutePath());
                return;
            }
        }

        if (!shopContainer.exists()) {
            shopContainer.mkdirs();
        }

        // Load profiles
        profiles.clear();
        for (File file : shopContainer.listFiles(new PrefixFileFilter(plugin, "shop"))) {
            final String id = file.getName().replace("shop_", "").replace(".yml", "").trim().toLowerCase();

            final YamlConfig config = new YamlConfig(plugin, file, false);
            config.load();

            final ShopProfile profile = new ShopProfile(plugin, id);

            try {
                profile.loadFrom(config);
            } catch (Exception ex) {
                logger.warning("Skipping shop profile: " + id + ". Exception loading profile!");
                logger.severe(ExceptionUtils.getFullStackTrace(ex));
            }

            if (!profile.isValid()) {
                logger.warning("Skipping shop profile: " + id + ". Invalid shop profile! (Are there missing entries?)");
                continue;
            }

            profiles.put(id, profile);
        }

        logger.info("Loaded " + profiles.size() + " shops");
    }

    @Override
    protected void onStop() {
        profiles.clear();
    }

}
