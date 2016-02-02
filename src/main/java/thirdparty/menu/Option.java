
package thirdparty.menu;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class Option {

    @Getter
    private final ItemStack stack;
    @Getter
    private final String name;

    public Option(ItemStack stack, String name) {
        this.stack = stack;
        this.name = name;
    }

}
