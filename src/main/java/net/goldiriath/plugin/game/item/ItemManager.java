package net.goldiriath.plugin.game.item;

import lombok.Getter;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.game.item.meta.GItemMeta;
import net.goldiriath.plugin.util.service.AbstractService;
import org.bukkit.inventory.ItemStack;

public class ItemManager extends AbstractService {

    @Getter
    private final ItemMetaManager itemMeta = new ItemMetaManager(plugin);
    @Getter
    private final CustomItemManager itemStorage = new CustomItemManager(plugin);

    public ItemManager(Goldiriath plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        itemMeta.start();
        itemStorage.start();
    }

    @Override
    protected void onStop() {
        itemStorage.stop();
        itemMeta.stop();
    }

    public ItemStack getItem(String id) {
        return itemStorage.getItemMap().get(id);
    }

    public GItemMeta getMeta(ItemStack stack, boolean create) {
        return itemMeta.getMeta(stack, create);
    }

    public boolean deleteMeta(ItemStack stack) {
        return itemMeta.deleteMeta(stack);
    }

}
