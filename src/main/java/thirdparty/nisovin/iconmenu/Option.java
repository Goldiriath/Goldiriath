package thirdparty.nisovin.iconmenu;

import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.stack);
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Option other = (Option) obj;
        if (!Objects.equals(this.stack, other.stack)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

}
