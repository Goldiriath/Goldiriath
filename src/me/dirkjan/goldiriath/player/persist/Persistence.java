package me.dirkjan.goldiriath.player.persist;

import java.lang.reflect.Field;

public class Persistence {

    private final Field field;
    private final ConfigDelegate delegate;
    private final boolean required;
    //
    private Object def;

    public Persistence(Field field, ConfigDelegate delegate, Object def, boolean required) {
        this.field = field;
        this.delegate = delegate;
        this.required = required;
    }

    public Field getField() {
        return field;
    }

    public ConfigDelegate getDelegate() {
        return delegate;
    }

    public Object getDefault() {
        return def;
    }

    public void setDefault(Object def) {
        this.def = def;
    }

    public boolean isRequired() {
        return required;
    }

}
