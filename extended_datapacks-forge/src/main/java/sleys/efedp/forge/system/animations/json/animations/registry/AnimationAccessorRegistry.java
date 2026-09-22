package sleys.efedp.forge.system.animations.json.animations.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class AnimationAccessorRegistry {
    private static final Map<String, IAnimationAccessorType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IAnimationAccessorType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private AnimationAccessorRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the events have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Animation Accessor - Registry] Attempting to register the animation accessor class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IAnimationAccessorType type = (IAnimationAccessorType) constant;
            String key = type.id().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) throw new IllegalStateException("[Animation Accessor - Registry] Duplicate Animation Accessor key: " + key);
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Animation Accessor - Registry] Registration completed successfully!");
    }

    public static IAnimationAccessorType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IAnimationAccessorType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Animation Accessor - Registry] Unknown animation accessor type: {} Applying recovery...", key);
        var defaultKey = "epicfight_edp:" + key;
        type = BY_KEY.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Animation Accessor - Registry] Successful recovery for animation accessor type: {}", defaultKey);
            return type;
        }

        throw new RegistryObjectModificationException(AnimationAccessorRegistryDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static final Codec<IAnimationAccessorType> CODEC = Codec.STRING.xmap(
            AnimationAccessorRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
