package sleys.efedp.neoforge.core.control.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ControlableSkillEventTypeRegistry {
    private static final Map<String, IControlableSkillEventType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IControlableSkillEventType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private ControlableSkillEventTypeRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the controlable events have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Controlable Event - Registry] Attempting to register the controlable event class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IControlableSkillEventType type = (IControlableSkillEventType) constant;
            String key = modId + ":" + constant.name().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) throw new IllegalStateException("[Controlable Event - Registry] Duplicate controlable event key: " + key);
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Controlable Event - Registry] Registration completed successfully!");
    }

    public static IControlableSkillEventType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IControlableSkillEventType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Controlable Event - Registry] Unknown controlable event type: {} Applying recovery...", key);
        var defaultKey = "epicfight_edp:" + key;
        type = BY_KEY.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Controlable Event - Registry] Successful recovery for controlable event type: {}", defaultKey);
            return type;
        }

        throw new RegistryObjectModificationException(ControlableSkillEventTypeRegistryDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static String keyOf(IControlableSkillEventType type) {
        return KEY_BY_INSTANCE.get(type);
    }

    public static final Codec<IControlableSkillEventType> CODEC = Codec.STRING.xmap(
            ControlableSkillEventTypeRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
