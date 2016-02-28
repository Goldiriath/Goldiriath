package net.goldiriath.plugin.player.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.goldiriath.plugin.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class DataFlags extends Data {

    private final Map<String, Integer> flags = new HashMap<>();

    public DataFlags(PlayerData data) {
        super(data, "flags");
    }

    public Map<String, Integer> getAll() {
        return Collections.unmodifiableMap(flags);
    }

    public int get(String flag) {
        final Integer integer = flags.get(flag);
        return integer == null ? 0 : integer;
    }

    public boolean has(String flag) {
        final Integer integer = flags.get(flag);
        return integer != null && integer > 0;
    }

    public void put(String flag, int amount) {
        if (amount == 0) {
            remove(flag);
            return;
        }

        flags.put(flag, amount);
    }

    public void remove(String flag) {
        flags.remove(flag);
    }

    public void increment(String flag) {
        increment(flag, 1);
    }

    public void increment(String flag, int amount) {
        put(flag, get(flag) + amount);
    }

    public void decrement(String flag) {
        increment(flag, -1);
    }

    public void decrement(String flag, int amount) {
        increment(flag, -amount);
    }

    @Override
    protected void load(ConfigurationSection config) {
        flags.clear();

        for (String flag : config.getKeys(false)) {
            final int amount = config.getInt(flag, 0);
            if (amount < 1) {
                continue;
            }

            flags.put(flag, amount);
        }
    }

    @Override
    protected void save(ConfigurationSection config) {
        for (String flag : flags.keySet()) {
            config.set(flag, flags.get(flag));
        }
    }

}
