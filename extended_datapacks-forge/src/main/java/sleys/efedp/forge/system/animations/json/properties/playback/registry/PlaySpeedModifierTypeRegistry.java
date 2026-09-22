package sleys.efedp.forge.system.animations.json.properties.playback.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;

import java.util.*;

public final class PlaySpeedModifierTypeRegistry {
    private static final Map<String, IPlaySpeedModifierType> BY_KEY = new LinkedHashMap<>();
    private static final Map<IPlaySpeedModifierType, String> KEY_BY_INSTANCE = new IdentityHashMap<>();

    private PlaySpeedModifierTypeRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the playbacks have been consumed by the accessors by the time this function is being called"
        );

        ExtendedDatapacks.LOGGER.info("[Play Speed Modifier - Registry] Attempting to register the playback speed modifier class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IPlaySpeedModifierType type = (IPlaySpeedModifierType) constant;
            String key = modId + ":" + constant.name().toLowerCase(Locale.ROOT);

            if (BY_KEY.putIfAbsent(key, type) != null) throw new IllegalStateException("[Play Speed Modifier - Registry] Duplicate playback speed modifier key: " + key);
            KEY_BY_INSTANCE.put(type, key);
        }

        ExtendedDatapacks.LOGGER.info("[Play Speed Modifier - Registry] Registration completed successfully!");
    }

    public static IPlaySpeedModifierType get(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IPlaySpeedModifierType type = BY_KEY.get(key);
        if (type != null) return type;

        ExtendedDatapacks.LOGGER.warn("[Play Speed Modifier - Registry] Unknown animation playback speed modifier type: {} Applying recovery...", key);
        var defaultKey = "epicfight_edp:" + key;
        type = BY_KEY.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Play Speed Modifier - Registry] Successful recovery for animation playback speed modifier type: {}", defaultKey);
            return type;
        }

        throw new RegistryObjectModificationException(PlaySpeedModifierRegistryDiagnostics.describeUnknownKey(key, BY_KEY));
    }

    public static final Codec<IPlaySpeedModifierType> CODEC = Codec.STRING.xmap(
            PlaySpeedModifierTypeRegistry::get,
            KEY_BY_INSTANCE::get
    );
}
