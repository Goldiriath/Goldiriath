package net.goldiriath.plugin.player.persist;

import java.lang.reflect.Field;
import lombok.Getter;
import lombok.Setter;
import net.goldiriath.plugin.player.persist.delegate.ConfigDelegate;

public class Persistence<T> {

    @Getter
    private final Field field;
    @Getter
    private final ConfigDelegate<T> delegate;
    //
    @Getter
    @Setter
    private Object defaultValue;

    public Persistence(Field field, ConfigDelegate<T> delegate, Object def) {
        this.field = field;
        this.delegate = delegate;
    }

}
