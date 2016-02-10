
package thirdparty.nisovin.iconmenu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OptionClickEvent {
    @Getter
    private final InventoryClickEvent event;
    @Getter
    private final Player player;
    @Getter
    private final int position;
    @Getter
    private final Option option;
    //
    @Getter
    @Setter
    private boolean close = true;
    @Getter
    @Setter
    private boolean destroy = false;
    public OptionClickEvent(InventoryClickEvent event, Player player, int position, Option option) {
        this.event = event;
        this.player = player;
        this.position = position;
        this.option = option;
    }

    public boolean hasOption() {
        return option != null;
    }



}
