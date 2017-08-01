package net.goldiriath.plugin.game.shop.menu;

import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.StaticItem;
import net.goldiriath.plugin.game.shop.ShopProfile;
import net.goldiriath.plugin.util.IconMenu;
import net.pravian.aero.component.PluginComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ChooseMenu extends PluginComponent<Goldiriath> implements IconMenu.OptionClickEventHandler {

    private final ShopProfile profile;

    public ChooseMenu(Goldiriath plugin, ShopProfile profile) {
        super(plugin);
        this.profile = profile;
    }

    public static void openMenu(Goldiriath plugin, ShopProfile profile, Player player) {
        int money = plugin.pm.getData(player).getMoney();
        ChooseMenu handler = new ChooseMenu(plugin, profile);
        IconMenu menu = new IconMenu(profile.getName() + " - Wallet: " + money + "Pm", 9, handler, plugin);

        menu.setOption(0, StaticItem.MENU_BUY_ITEMS.getStack(), "buy");
        menu.setOption(8, StaticItem.MENU_SELL_ITEMS.getStack(), "sell");

        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        if ("buy".equals(event.getName())) {
            BuyMenu.openMenu(plugin, profile, event.getPlayer());
        } else if ("sell".equals(event.getName())) {
            SellMenu.openMenu(plugin, profile, event.getPlayer());
        }
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
