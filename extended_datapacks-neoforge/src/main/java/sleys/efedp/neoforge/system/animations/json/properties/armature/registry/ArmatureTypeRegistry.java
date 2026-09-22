package sleys.efedp.neoforge.system.animations.json.properties.armature.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.bootstrap.Bootstrap;
import sleys.sl.library.exceptions.RegistryObjectException;
import sleys.sl.library.exceptions.RegistryObjectModificationException;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

import java.util.*;

public final class ArmatureTypeRegistry {
    private static final Map<String, IArmatureType> BY_ID = new LinkedHashMap<>();
    private static final Map<AssetAccessor<? extends Armature>, IArmatureType> BY_ACCESSOR = new IdentityHashMap<>();

    private ArmatureTypeRegistry() {}

    public static void register(String modId, Class<? extends Enum<?>> enumClass) {
        if (Bootstrap.isClosedRegistry()) throw new RegistryObjectException(
                "Attempted illegal registration, the operation cannot be completed because the armatures have been consumed by the accessors by the time this function is being called"
        );


        ExtendedDatapacks.LOGGER.info("[Armature - Registry] Attempting to register the armatures class: {} for: {}", enumClass.getSimpleName(), modId);
        for (Enum<?> constant : enumClass.getEnumConstants()) {
            IArmatureType type = (IArmatureType) constant;
            String key = type.id().toLowerCase(Locale.ROOT);

            if (BY_ID.putIfAbsent(key, type) != null) throw new IllegalStateException("[Armature - Registry] Duplicate armature type id: " + key);
            var previous = BY_ACCESSOR.putIfAbsent(type.armature(), type);
            if (previous != null) ExtendedDatapacks.LOGGER.warn("[Armature - Registry] {} shares its armature with {}", key, previous.id());
        }

        ExtendedDatapacks.LOGGER.info("[Armature - Registry] Registration completed successfully!");
    }

    public static Optional<IArmatureType> find(String key) {
        key = key.toLowerCase(Locale.ROOT);
        IArmatureType type = BY_ID.get(key);
        if (type != null) return Optional.of(type);

        ExtendedDatapacks.LOGGER.warn("[Armature - Registry] Unknown animation armature type: {} Applying recovery...", key);
        var defaultKey = "epicfight:entity/" + key;
        type = BY_ID.get(defaultKey);

        if (type != null) {
            ExtendedDatapacks.LOGGER.info("[Armature - Registry] Successful recovery for animation armature type: {}", defaultKey);
            return Optional.of(type);
        }

        throw new RegistryObjectModificationException(ArmatureTypeRegistryDiagnostics.describeUnknownKey(key, BY_ID));
    }

    public static Optional<IArmatureType> fromAccessor(AssetAccessor<? extends Armature> accessor) {
        return Optional.ofNullable(BY_ACCESSOR.get(accessor));
    }

    public static final Codec<IArmatureType> CODEC = Codec.STRING.comapFlatMap(
            key -> find(key)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown armature type: " + key + ". Known: " + BY_ID.keySet())),
            IArmatureType::id
    );
}