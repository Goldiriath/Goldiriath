package net.goldiriath.plugin.shop.menu;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.shop.Product;
import net.goldiriath.plugin.shop.ProductAction;
import net.goldiriath.plugin.shop.ShopProfile;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

public class BuyMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private final ShopProfile profile;

    public BuyMenu(Goldiriath plugin, ShopProfile profile) {
        super(plugin);
        this.profile = profile;
    }

    public void openMenu(Player player) {
        final IconMenu menu = new IconMenu("Buy items", 27, this, plugin);

        int slot = 0;
        for (Product product : profile.getProducts()) {
            if (product.getAction() == ProductAction.SELL) {
                continue;
            }

            menu.setOption(slot, product.getDisplayStack(), product.toString());
            slot++;
        }

        // Exit button
        menu.setOption(26, StaticItem.MENU_DONE.getStack(), "done");

        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        // TODO handle buying
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
