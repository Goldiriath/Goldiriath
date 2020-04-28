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
    private final Runnable callback;

    public ChooseMenu(Goldiriath plugin, ShopProfile profile, Runnable callback) {
        super(plugin);
        this.profile = profile;
        this.callback = callback;
    }

    public static void openMenu(Goldiriath plugin, ShopProfile profile, Player player, Runnable callback) {
        int money = plugin.pym.getData(player).getMoney();
        ChooseMenu handler = new ChooseMenu(plugin, profile, callback);
        IconMenu menu = new IconMenu(profile.getName() + " - Wallet: " + money + "Pm", 9, handler, plugin);

        menu.setOption(0, StaticItem.MENU_BUY_ITEMS.getStack(), "buy");
        menu.setOption(8, StaticItem.MENU_SELL_ITEMS.getStack(), "sell");

        menu.open(player);
    }

    @Override
    public void onOptionClick(IconMenu.OptionClickEvent event) {
        if ("buy".equals(event.getName())) {
            BuyMenu.openMenu(plugin, profile, event.getPlayer(), callback);
        } else if ("sell".equals(event.getName())) {
            SellMenu.openMenu(plugin, profile, event.getPlayer(), callback);
        }
    }

    @Override
    public void onOptionDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

}
