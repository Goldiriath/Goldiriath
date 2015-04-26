package net.goldiriath.plugin.player.info.modifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.player.info.Info;

public class InfoEffects extends Info {

    private List<Effect> modifiers = new ArrayList<>();

    public InfoEffects(PlayerData data) {
        super(data);
    }

    public void addModifier(Effect modifier) {
        modifiers.add(modifier);
    }

    public List<Effect> getActiveModifiers() {
        Iterator<Effect> mi = modifiers.iterator();
        while (mi.hasNext()) {
            if (mi.next().isOver()) {
                mi.remove();
            }
        }

        return modifiers;
    }

}
