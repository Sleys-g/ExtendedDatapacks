package sleys.efedp.forge.core.events.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;

import java.util.*;

public final class EventTypeRegistry {
    private static final Map<String, IEventType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IEventType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private EventTypeRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the events have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Attempting to register the animation event class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IEventType type = (IEventType) constant;
            String key = modId + ":" + constant.name().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) throw new IllegalStateException("[Animation Event - Registry] Duplicate animation event key: " + key);
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Registration completed successfully!");
    }

    public static IEventType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IEventType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Animation Event - Registry] Unknown animation event type: {} Applying recovery...", key);
        var defaultKey = "epicfight_edp:" + key;
        type = BY_KEY.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Animation Event - Registry] Successful recovery for animation event type: {}", defaultKey);
            return type;
        }

        throw new RegistryObjectModificationException(EventTypeRegistryDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static final Codec<IEventType> CODEC = Codec.STRING.xmap(
            EventTypeRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
