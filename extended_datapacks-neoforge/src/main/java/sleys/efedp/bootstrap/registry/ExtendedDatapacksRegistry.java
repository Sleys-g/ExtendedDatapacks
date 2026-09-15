package sleys.efedp.bootstrap.registry;

import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.properties.functional.playback.IPlaySpeedModifierType;
import sleys.efedp.system.animations.json.properties.functional.playback.registry.PlaySpeedModifierTypeRegistry;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.registry.AnimationEventTypeRegistry;

import java.util.function.BiConsumer;

public enum ExtendedDatapacksRegistry {
    ANIMATIONS_EVENTS(ExtendedDatapacksRegistry::onRegisterAnimationsEvents),
    ANIMATION_PLAYBACK(ExtendedDatapacksRegistry::onRegisterPlaySpeedModifier),

    ;private final BiConsumer<String, Class<? extends Enum<?>>> process;

    ExtendedDatapacksRegistry(BiConsumer<String, Class<? extends Enum<?>>> process) {
        this.process = process;
    }

    public void register(String modId, Class<? extends Enum<?>> enumClass) {
        this.process.accept(modId, enumClass);
    }

    @SafeVarargs
    public final void register(String modId, Class<? extends Enum<?>>... enumClasses) {
        for (var enumClass : enumClasses) this.process.accept(modId, enumClass);
    }

    private static void onRegisterAnimationsEvents(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IAnimationEventType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Event] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationEventType; therefore, registration was prevented...",
                    enumClass.getSimpleName(), modId
            );
            return;
        }

        AnimationEventTypeRegistry.register(modId, enumClass);
    }

    private static void onRegisterPlaySpeedModifier(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IPlaySpeedModifierType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Play Speed Modifier] The namespace '{}' attempted to register a class '{}' that is not an instance of IPlaySpeedModifierType; therefore, registration was prevented...",
                    enumClass.getSimpleName(), modId
            );

            return;
        }
        PlaySpeedModifierTypeRegistry.register(modId, enumClass);
    }
}
