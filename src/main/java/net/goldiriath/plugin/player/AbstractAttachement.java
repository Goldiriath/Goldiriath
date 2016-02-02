
package net.goldiriath.plugin.player;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.util.logging.GLogger;

public abstract class AbstractAttachement {

    protected final Goldiriath plugin;
    protected final PlayerData data;
    protected final GLogger logger;

    public AbstractAttachement(PlayerData data) {
        this.plugin = data.getPlugin();
        this.data = data;
        this.logger = plugin.logger;
    }

}
