package net.goldiriath.plugin.item.meta;

import java.util.ArrayList;
import java.util.List;
import net.goldiriath.plugin.persist.Persist;
import net.goldiriath.plugin.persist.PersistentStorage;

@SuppressWarnings("FieldMayBeFinal")
public class ItemMeta extends PersistentStorage {

    @Persist
    private String name = null;

    @Persist
    private List<String> lore = new ArrayList<>();

}
