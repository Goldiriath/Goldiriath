package thirdparty.de.slikey.effectlib;

import thirdparty.de.slikey.effectlib.math.Transforms;
import thirdparty.de.slikey.effectlib.util.ConfigUtils;
import thirdparty.de.slikey.effectlib.util.Disposable;
import thirdparty.de.slikey.effectlib.util.DynamicLocation;
import thirdparty.de.slikey.effectlib.util.ImageLoadCallback;
import thirdparty.de.slikey.effectlib.util.ImageLoadTask;
import thirdparty.de.slikey.effectlib.util.ParticleDisplay;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.base.CaseFormat;

/**
 * Dispose the EffectManager if you don't need him anymore.
 *
 * @author Kevin
 *
 */
public class EffectManager implements Disposable {

    private static List<EffectManager> effectManagers;
    private static Map<String, Class<? extends Effect>> effectClasses = new HashMap<String, Class<? extends Effect>>();
    private final Plugin owningPlugin;
    private final Map<Effect, BukkitTask> effects;
    private ParticleDisplay display;
    private boolean disposed;
    private boolean disposeOnTermination;
    private boolean debug = false;
    private int visibleRange = 32;
    private File imageCacheFolder;
    private Map<String, BufferedImage[]> imageCache = new HashMap<String, BufferedImage[]>();

    public EffectManager(Plugin owningPlugin) {
        imageCacheFolder = owningPlugin == null ? null : new File(owningPlugin.getDataFolder(), "imagecache");
        this.owningPlugin = owningPlugin;
        Transforms.setEffectManager(this);
        effects = new HashMap<Effect, BukkitTask>();
        disposed = false;
        disposeOnTermination = false;
    }

    private ParticleDisplay getDisplay() {
        if (display == null) {
            display = ParticleDisplay.newInstance();
        }
        display.setManager(this);

        return display;
    }

    public void display(Particle particle, Location center, float offsetX, float offsetY, float offsetZ, float speed, int amount, float size, Color color, Material material, byte materialData, double range, List<Player> targetPlayers) {
        getDisplay().display(particle, center, offsetX, offsetY, offsetZ, speed, amount, size, color, material, materialData, range, targetPlayers);
    }

    public void start(Effect effect) {
        if (disposed) {
            throw new IllegalStateException("EffectManager is disposed and not able to accept any effects.");
        }
        if (disposeOnTermination) {
            throw new IllegalStateException("EffectManager is awaiting termination to dispose and not able to accept any effects.");
        }

        if (effects.containsKey(effect)) {
            effect.cancel(false);
        }

        if (!owningPlugin.isEnabled()) {
            return;
        }

        BukkitScheduler s = Bukkit.getScheduler();
        BukkitTask task = null;
        switch (effect.getType()) {
            case INSTANT:
                if (effect.isAsynchronous()) {
                    task = s.runTaskAsynchronously(owningPlugin, effect);
                } else {
                    task = s.runTask(owningPlugin, effect);
                }
                break;
            case DELAYED:
                if (effect.isAsynchronous()) {
                    task = s.runTaskLaterAsynchronously(owningPlugin, effect, effect.getDelay());
                } else {
                    task = s.runTaskLater(owningPlugin, effect, effect.getDelay());
                }
                break;
            case REPEATING:
                if (effect.isAsynchronous()) {
                    task = s.runTaskTimerAsynchronously(owningPlugin, effect, effect.getDelay(), effect.getPeriod());
                } else {
                    task = s.runTaskTimer(owningPlugin, effect, effect.getDelay(), effect.getPeriod());
                }
                break;
        }
        synchronized (this) {
            effects.put(effect, task);
        }
    }

    public Effect start(String effectClass, ConfigurationSection parameters, Location origin, Entity originEntity) {
        return start(effectClass, parameters, origin, null, originEntity, null, null);
    }

    public Effect start(String effectClass, ConfigurationSection parameters, Entity originEntity) {
        return start(effectClass, parameters, originEntity == null ? null : originEntity.getLocation(), null, originEntity, null, null);
    }

    public Effect start(String effectClass, ConfigurationSection parameters, Location origin) {
        return start(effectClass, parameters, origin, null, null, null, null);
    }

    public Effect start(String effectClass, ConfigurationSection parameters, Location origin, Player targetPlayer) {
        return start(effectClass, parameters, new DynamicLocation(origin, null), new DynamicLocation(null, null), (ConfigurationSection) null, targetPlayer);
    }

    /**
     * Start an Effect from a Configuration map of parameters.
     *
     * @param effectClass The name of the Effect class to instantiate. If unqualified, defaults to the de.slikey.effectlib.effect namespace.
     * @param parameters A Configuration-driven map of key/value parameters. Each of these will be applied directly to the corresponding field in the Effect instance.
     * @param origin The origin Location
     * @param target The target Location, only used in some Effects (like LineEffect)
     * @param originEntity The origin Entity, the effect will attach to the Entity's Location
     * @param targetEntity The target Entity, only used in some Effects
     * @param parameterMap A map of parameter values to replace. These must start with the "$" character, values in the parameters map that contain a $key will be replaced with the value in this
     * parameterMap.
     * @return
     */
    @Deprecated
    public Effect start(String effectClass, ConfigurationSection parameters, Location origin, Location target, Entity originEntity, Entity targetEntity, Map<String, String> parameterMap) {
        return start(effectClass, parameters, new DynamicLocation(origin, originEntity), new DynamicLocation(target, targetEntity), parameterMap);
    }

    /**
     * Start an Effect from a Configuration map of parameters.
     *
     * @param effectClass The name of the Effect class to instantiate. If unqualified, defaults to the de.slikey.effectlib.effect namespace.
     * @param parameters A Configuration-driven map of key/value parameters. Each of these will be applied directly to the corresponding field in the Effect instance.
     * @param origin The origin Location
     * @param target The target Location, only used in some Effects (like LineEffect)
     * @param parameterMap A map of parameter values to replace. These must start with the "$" character, values in the parameters map that contain a $key will be replaced with the value in this
     * parameterMap.
     * @return
     */
    @Deprecated
    public Effect start(String effectClass, ConfigurationSection parameters, DynamicLocation origin, DynamicLocation target, Map<String, String> parameterMap) {
        return start(effectClass, parameters, origin, target, parameterMap, null);
    }

    public Effect getEffectByClassName(String effectClass) {
        Class<? extends Effect> effectLibClass;
        try {
            // First check the name as given
            effectLibClass = effectClasses.get(effectClass);

            // A shaded manager may provide a fully-qualified path.
            if (effectLibClass == null && !effectClass.contains(".")) {
                effectClass = "de.slikey.effectlib.effect." + effectClass;
                if (!effectClass.endsWith("Effect")) {
                    effectClass = effectClass + "Effect";
                }
                effectLibClass = effectClasses.get(effectClass);
            }
            if (effectLibClass == null) {
                effectLibClass = (Class<? extends Effect>) Class.forName(effectClass);
                effectClasses.put(effectClass, effectLibClass);
            }
        } catch (Throwable ex) {
            onError("Error loading EffectLib class: " + effectClass, ex);
            return null;
        }

        Effect effect = null;
        try {
            Constructor constructor = effectLibClass.getConstructor(EffectManager.class);
            effect = (Effect) constructor.newInstance(this);
        } catch (Exception ex) {
            onError("Error loading EffectLib class: " + effectClass, ex);
        }

        return effect;
    }

    public Effect getEffect(String effectClass, ConfigurationSection parameters, DynamicLocation origin, DynamicLocation target, ConfigurationSection parameterMap, Player targetPlayer) {
        Effect effect = getEffectByClassName(effectClass);
        if (effect == null) {
            return null;
        }

        Collection<String> keys = parameters.getKeys(false);
        for (String key : keys) {
            if (key.equals("class")) {
                continue;
            }

            if (!setField(effect, key, parameters, parameterMap) && debug) {
                owningPlugin.getLogger().warning("Unable to assign EffectLib property " + key + " of class " + effect.getClass().getName());
            }
        }

        effect.setDynamicOrigin(origin);
        effect.setDynamicTarget(target);

        if (targetPlayer != null) {
            effect.setTargetPlayer(targetPlayer);
        }

        return effect;
    }

    @Deprecated
    public Effect start(String effectClass, ConfigurationSection parameters, DynamicLocation origin, DynamicLocation target, Map<String, String> parameterMap, Player targetPlayer) {
        ConfigurationSection configMap = null;
        if (parameterMap != null) {
            configMap = ConfigUtils.toStringConfiguration(parameterMap);
        }

        return start(effectClass, parameters, origin, target, configMap, targetPlayer);
    }

    /**
     * Start an effect, possibly using parameter replacement.
     *
     * @param effectClass the effect class to start
     * @param parameters any parameters to pass to the effect
     * @param origin the origin location
     * @param target the target location
     * @param parameterMap a configuration of variables from the parameter config to replace
     * @param targetPlayer The player who should see this effect.
     * @return
     */
    public Effect start(String effectClass, ConfigurationSection parameters, DynamicLocation origin, DynamicLocation target, ConfigurationSection parameterMap, Player targetPlayer) {
        Effect effect = getEffect(effectClass, parameters, origin, target, parameterMap, targetPlayer);
        if (effect == null) {
            return null;
        }
        effect.start();
        return effect;
    }

    public void cancel(boolean callback) {
        List<Effect> allEffects = new ArrayList<Effect>(effects.keySet());
        for (Effect effect : allEffects) {
            effect.cancel(callback);
        }
    }

    public void done(Effect effect) {
        synchronized (this) {
            BukkitTask existingTask = effects.get(effect);
            if (existingTask != null) {
                existingTask.cancel();
            }
            effects.remove(effect);
        }
        if (effect.callback != null && owningPlugin.isEnabled()) {
            Bukkit.getScheduler().runTask(owningPlugin, effect.callback);
        }
        if (disposeOnTermination && effects.isEmpty()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        cancel(false);
        if (effectManagers != null) {
            effectManagers.remove(this);
        }
    }

    public void disposeOnTermination() {
        disposeOnTermination = true;
        if (effects.isEmpty()) {
            dispose();
        }
    }

    public void enableDebug(boolean enable) {
        debug = enable;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public void onError(Throwable ex) {
        if (debug) {
            owningPlugin.getLogger().log(Level.WARNING, "Particle Effect error", ex);
        }
    }

    public void onError(String message) {
        if (debug) {
            owningPlugin.getLogger().log(Level.WARNING, message);
        }
    }

    public void onError(String message, Throwable ex) {
        if (debug) {
            owningPlugin.getLogger().log(Level.WARNING, message, ex);
        }
    }

    public int getParticleRange() {
        return visibleRange;
    }

    public void setParticleRange(int range) {
        visibleRange = range;
    }

    public Plugin getOwningPlugin() {
        return owningPlugin;
    }

    protected boolean setField(Object effect, String key, ConfigurationSection section, ConfigurationSection parameterMap) {
        try {
            String stringValue = section.getString(key);
            String fieldKey = key;

            // Allow underscore_style and dash_style parameters
            if (key.contains("-")) {
                key = key.replace("-", "_");
            }
            if (key.contains("_")) {
                key = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);
            }

            ConfigurationSection fieldSection = section;
            if (parameterMap != null && stringValue.startsWith("$") && parameterMap.contains(stringValue)) {
                fieldKey = stringValue;
                fieldSection = parameterMap;
            }
            Field field = effect.getClass().getField(key);
            if (field.getType().equals(Integer.TYPE) || field.getType().equals(Integer.class)) {
                field.set(effect, fieldSection.getInt(fieldKey));
            } else if (field.getType().equals(Float.TYPE) || field.getType().equals(Float.class)) {
                field.set(effect, (float) fieldSection.getDouble(fieldKey));
            } else if (field.getType().equals(Double.TYPE) || field.getType().equals(Double.class)) {
                field.set(effect, fieldSection.getDouble(fieldKey));
            } else if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class)) {
                field.set(effect, fieldSection.getBoolean(fieldKey));
            } else if (field.getType().equals(Long.TYPE) || field.getType().equals(Long.class)) {
                field.set(effect, fieldSection.getLong(fieldKey));
            } else if (field.getType().equals(Short.TYPE) || field.getType().equals(Short.class)) {
                field.set(effect, (short) fieldSection.getInt(fieldKey));
            } else if (field.getType().equals(Byte.TYPE) || field.getType().equals(Byte.class)) {
                field.set(effect, (byte) fieldSection.getInt(fieldKey));
            } else if (field.getType().equals(String.class)) {
                String value = fieldSection.getString(fieldKey);
                field.set(effect, value);
            } else if (field.getType().equals(Color.class)) {
                try {
                    String value = fieldSection.getString(fieldKey);
                    Integer rgb = Integer.parseInt(value, 16);
                    Color color = Color.fromRGB(rgb);
                    field.set(effect, color);
                } catch (Exception ex) {
                    onError(ex);
                }
            } else if (Map.class.isAssignableFrom(field.getType()) && section.isConfigurationSection(key)) {
                Map<String, Object> map = (Map<String, Object>) field.get(effect);
                ConfigurationSection subSection = section.getConfigurationSection(key);
                Set<String> keys = subSection.getKeys(false);
                for (String mapKey : keys) {
                    map.put(mapKey, subSection.get(mapKey));
                }
            } else if (Map.class.isAssignableFrom(field.getType()) && Map.class.isAssignableFrom(section.get(key).getClass())) {
                field.set(effect, section.get(key));
            } else if (ConfigurationSection.class.isAssignableFrom(field.getType())) {
                ConfigurationSection configSection = ConfigUtils.getConfigurationSection(section, key);
                if (parameterMap != null) {
                    ConfigurationSection baseConfiguration = configSection;
                    configSection = new MemoryConfiguration();
                    Set<String> keys = baseConfiguration.getKeys(false);
                    // Note this doesn't handle sections within sections.
                    for (String baseKey : keys) {
                        Object baseValue = baseConfiguration.get(baseKey);
                        if (baseValue instanceof String && ((String) baseValue).startsWith("$")) {
                            // If this is an equation it will get parsed when needed
                            String parameterValue = parameterMap.getString((String) baseValue);
                            baseValue = parameterValue == null ? baseValue : parameterValue;
                        }
                        configSection.set(baseKey, baseValue);
                    }
                }
                field.set(effect, configSection);
            } else if (field.getType().equals(Vector.class)) {
                double x = 0;
                double y = 0;
                double z = 0;
                try {
                    String value = fieldSection.getString(fieldKey);
                    String[] pieces = value.split(",");
                    x = pieces.length > 0 ? Double.parseDouble(pieces[0]) : 0;
                    y = pieces.length > 1 ? Double.parseDouble(pieces[1]) : 0;
                    z = pieces.length > 2 ? Double.parseDouble(pieces[2]) : 0;
                } catch (Exception ex) {
                    onError(ex);
                }
                field.set(effect, new Vector(x, y, z));
            } else if (field.getType().isEnum()) {
                Class<Enum> enumType = (Class<Enum>) field.getType();
                try {
                    String value = fieldSection.getString(fieldKey);
                    Enum enumValue = Enum.valueOf(enumType, value.toUpperCase());
                    field.set(effect, enumValue);
                } catch (Exception ex) {
                    onError(ex);
                }
            } else if (field.getType().equals(Font.class)) {
                try {
                    // Should caching the fonts be considered?
                    // Or is the performance gain negligible?
                    String value = fieldSection.getString(fieldKey);
                    Font font = Font.decode(value);
                    field.set(effect, font);
                } catch (Exception ex) {
                    onError(ex);
                }
            } else {
                return false;
            }

            return true;
        } catch (Exception ex) {
            this.onError(ex);
        }

        return false;
    }

    public static void initialize() {
        effectManagers = new ArrayList<EffectManager>();
    }

    public static List<EffectManager> getManagers() {
        if (effectManagers == null) {
            initialize();
        }
        return effectManagers;
    }

    public static void disposeAll() {
        if (effectManagers != null) {
            for (Iterator<EffectManager> i = effectManagers.iterator(); i.hasNext();) {
                EffectManager em = i.next();
                i.remove();
                em.dispose();
            }
        }
    }

    public void setImageCacheFolder(File folder) {
        imageCacheFolder = folder;
    }

    public File getImageCacheFolder() {
        return imageCacheFolder;
    }

    public void loadImage(final String fileName, final ImageLoadCallback callback) {
        BufferedImage[] images = imageCache.get(fileName);
        if (images != null) {
            callback.loaded(images);
            return;
        }

        owningPlugin.getServer().getScheduler().runTaskAsynchronously(owningPlugin, new ImageLoadTask(this, fileName, new ImageLoadCallback() {
            @Override
            public void loaded(final BufferedImage[] images) {
                owningPlugin.getServer().getScheduler().runTask(owningPlugin, new Runnable() {
                    @Override
                    public void run() {
                        imageCache.put(fileName, images);
                        callback.loaded(images);
                    }
                });
            }
        }));
    }

    public void registerEffectClass(String key, Class<? extends Effect> effectClass) {
        effectClasses.put(key, effectClass);
    }
}
