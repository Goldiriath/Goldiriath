package net.goldiriath.plugin;

import lombok.Getter;
import net.goldiriath.plugin.util.service.AbstractService;
import thirdparty.de.slikey.effectlib.EffectManager;

public class EffectLibBridge extends AbstractService {

    @Getter
    private EffectManager manager;

    public EffectLibBridge(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        EffectManager.initialize();
        manager = new EffectManager(plugin);
    }

    @Override
    protected void onStop() {
        manager.dispose();
    }

}
