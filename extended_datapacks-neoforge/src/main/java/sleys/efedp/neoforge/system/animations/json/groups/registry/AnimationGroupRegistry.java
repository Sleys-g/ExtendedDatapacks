package sleys.efedp.neoforge.system.animations.json.groups.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class AnimationGroupRegistry {
    private static final Map<String, IAnimationGroupType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IAnimationGroupType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private AnimationGroupRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the events have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Animation Group - Registry] Attempting to register the animation group class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IAnimationGroupType type = (IAnimationGroupType) constant;
            String key = type.id().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) throw new IllegalStateException("[Animation Group - Registry] Duplicate Animation Group key: " + key);
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Animation Group - Registry] Registration completed successfully!");
    }

    public static IAnimationGroupType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IAnimationGroupType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Animation Group - Registry] Unknown animation group type: {} Applying recovery...", key);
        var defaultKey = "epicfight_edp:" + key;
        type = BY_KEY.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Animation Group - Registry] Successful recovery for animation group type: {}", defaultKey);
            return type;
        }

        throw new RegistryObjectModificationException(AnimationGroupDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static final Codec<IAnimationGroupType> CODEC = Codec.STRING.xmap(
            AnimationGroupRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
