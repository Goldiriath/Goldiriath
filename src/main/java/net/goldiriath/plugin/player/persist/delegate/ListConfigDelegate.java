package net.goldiriath.plugin.player.persist.delegate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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
        return config.getList(key);
    }

}
