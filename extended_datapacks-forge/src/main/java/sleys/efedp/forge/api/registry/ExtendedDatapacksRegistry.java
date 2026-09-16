package sleys.efedp.forge.api.registry;

import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.json.animations.registry.AnimationAccessorRegistry;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.groups.registry.AnimationGroupRegistry;
import sleys.efedp.forge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.forge.system.animations.json.properties.playback.registry.IPlaySpeedModifierType;
import sleys.efedp.forge.system.animations.json.properties.playback.registry.PlaySpeedModifierTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.time.registry.AnimationEventTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.time.registry.IAnimationEventType;

import java.util.function.BiConsumer;

public enum ExtendedDatapacksRegistry {
    ANIMATIONS_EVENTS(ExtendedDatapacksRegistry::onRegisterAnimationsEvents),
    ANIMATION_PLAYBACK(ExtendedDatapacksRegistry::onRegisterPlaySpeedModifier),
    ANIMATION_ACCESSOR(ExtendedDatapacksRegistry::onRegisterAnimationsAccessor),
    ANIMATION_GROUP(ExtendedDatapacksRegistry::onRegisterAnimationsGroups),

    ;private final BiConsumer<String, Class<? extends Enum<?>>> registryProcess;

    ExtendedDatapacksRegistry(BiConsumer<String, Class<? extends Enum<?>>> registryProcess) {
        this.registryProcess = registryProcess;
    }

    public void register(String modId, Class<? extends Enum<?>> enumClass) {
        this.registryProcess.accept(modId, enumClass);
    }

    @SafeVarargs
    public final void register(String modId, Class<? extends Enum<?>>... enumClasses) {
        for (var enumClass : enumClasses) this.registryProcess.accept(modId, enumClass);
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

    private static void onRegisterAnimationsAccessor(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IAnimationAccessorType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Accessor] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationAccessorType; therefore, registration was prevented...",
                    enumClass.getSimpleName(), modId
            );

            return;
        }

        AnimationAccessorRegistry.register(modId, enumClass);
    }

    private static void onRegisterAnimationsGroups(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IAnimationGroupType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Group] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationGroupType; therefore, registration was prevented...",
                    enumClass.getSimpleName(), modId
            );

            return;
        }

        AnimationGroupRegistry.register(modId, enumClass);
    }
}
