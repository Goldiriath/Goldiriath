package net.goldiriath.plugin.util.persist.delegate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;

public class ListConfigDelegate extends ConfigDelegate<List<?>> {

    public ListConfigDelegate(String key) {
        super(key);
    }

    @Override
    public List<?> loadValue(ConfigurationSection config, Field field) { // TODO: Find a better way
        if (!config.isList(key)) {
            return null;
        }

        final Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            return config.getList(key);
        }

        final Class<?> arg = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        if (arg == null) {
            return config.getList(key);
        }

        if (arg.equals(String.class)) {
            return config.getStringList(key);
        }

        if (arg.equals(Integer.class)) {
            return config.getIntegerList(key);
        }

        if (arg.equals(Byte.class)) {
            return config.getByteList(key);
        }

        if (arg.equals(Float.class)) {
            return config.getFloatList(key);
        }

        if (arg.equals(Double.class)) {
            return config.getDoubleList(key);
        }

        if (arg.equals(Long.class)) {
            return config.getLongList(key);
        }

        if (arg.equals(Character.class)) {
            return config.getCharacterList(key);
        }

        if (arg.equals(Short.class)) {
            return config.getShortList(key);
        }

        if (arg.equals(Boolean.class)) {
            return config.getBooleanList(key);
        }

        if (arg.equals(Map.class)) {
            return config.getMapList(key);
        }

        return config.getList(key);
    }

}
