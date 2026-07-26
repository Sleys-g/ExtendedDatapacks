package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.Codec;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;

import java.util.*;

public final class AnimationEventTypeRegistry {
    private static final Map<String, IAnimationEventType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IAnimationEventType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private AnimationEventTypeRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the events have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Attempting to register the event class: {} for: {}", enumClass.getName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IAnimationEventType type = (IAnimationEventType) constant;
            String key = modId + ":" + constant.name().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) {
                throw new IllegalStateException("Duplicate animation event key: " + key);
            }
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Registration completed successfully!");
    }

    public static IAnimationEventType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IAnimationEventType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Animation Event - Registry] Unknown animation event type: {} Applying recovery...", key);
        type = BY_KEY.get("epicfight_edp:" + key);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Successful recovery for event type: {}", key);
            return type;
        }

        throw new NoSuchElementException(AnimationEventTypeRegistryDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static final Codec<IAnimationEventType> CODEC = Codec.STRING.xmap(
            AnimationEventTypeRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
