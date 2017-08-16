package net.goldiriath.plugin.player.info;

import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.player.PlayerData;
import net.goldiriath.plugin.wand.Element;
import org.bukkit.inventory.ItemStack;

/**
 * Created by koen on 04/08/2017.
 */
public class InfoWand extends Info {

    @Getter
    @Setter
    private boolean choosing;
    @Getter
    @Setter
    private ItemStack[] storedItems;
    private Element wandElement;
    private long elementExpires;

    public InfoWand(PlayerData data) {
        super(data);
        choosing = false;
    }

    public void setWandElement(Element element, long durationInSeconds) {
        elementExpires = System.currentTimeMillis() + (durationInSeconds * 1000);
        this.wandElement = element;
    }
    public Element getWandElement() {
        if (System.currentTimeMillis() > elementExpires) {
            wandElement = Element.None;
        }
        return wandElement;
    }

}

