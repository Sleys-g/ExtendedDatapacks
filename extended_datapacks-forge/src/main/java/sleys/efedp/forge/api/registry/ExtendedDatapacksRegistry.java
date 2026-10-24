package sleys.efedp.forge.api.registry;

import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.core.control.registry.ControlableEventTypeRegistry;
import sleys.efedp.forge.core.control.registry.IControlableEventType;
import sleys.efedp.forge.system.animations.animations.registry.AnimationAccessorRegistry;
import sleys.efedp.forge.system.animations.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.groups.registry.AnimationGroupRegistry;
import sleys.efedp.forge.system.animations.groups.registry.IAnimationGroupType;
import sleys.efedp.forge.system.animations.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.properties.armature.registry.IArmatureType;
import sleys.efedp.forge.system.animations.properties.playback.registry.IPlaySpeedModifierType;
import sleys.efedp.forge.system.animations.properties.playback.registry.PlaySpeedModifierTypeRegistry;
import sleys.efedp.forge.core.events.registry.EventTypeRegistry;
import sleys.efedp.forge.core.events.registry.IEventType;

import java.util.function.BiConsumer;

public enum ExtendedDatapacksRegistry {
    EVENTS(ExtendedDatapacksRegistry::onRegisterEvents),
    CONTROLABLE_EVENTS(ExtendedDatapacksRegistry::onRegisterControlableEvents),

    ANIMATION_ARMATURE(ExtendedDatapacksRegistry::onRegisterAnimationsArmatures),
    ANIMATION_ACCESSOR(ExtendedDatapacksRegistry::onRegisterAnimationsAccessor),
    ANIMATION_GROUP(ExtendedDatapacksRegistry::onRegisterAnimationsGroups),
    ANIMATION_PLAYBACK(ExtendedDatapacksRegistry::onRegisterPlaySpeedModifier)

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

    private static void onRegisterEvents(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IEventType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Event] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationEventType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );
            return;
        }

        EventTypeRegistry.register(modId, enumClass);
    }

    private static void onRegisterControlableEvents(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IControlableEventType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Controlable Events] The namespace '{}' attempted to register a class '{}' that is not an instance of IControlableSkillEventType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );

            return;
        }

        ControlableEventTypeRegistry.register(modId, enumClass);
    }

    private static void onRegisterPlaySpeedModifier(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IPlaySpeedModifierType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Play Speed Modifier] The namespace '{}' attempted to register a class '{}' that is not an instance of IPlaySpeedModifierType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );

            return;
        }

        PlaySpeedModifierTypeRegistry.register(modId, enumClass);
    }

    private static void onRegisterAnimationsAccessor(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IAnimationAccessorType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Accessor] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationAccessorType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );

            return;
        }

        AnimationAccessorRegistry.register(modId, enumClass);
    }

    private static void onRegisterAnimationsGroups(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IAnimationGroupType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Group] The namespace '{}' attempted to register a class '{}' that is not an instance of IAnimationGroupType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );

            return;
        }

        AnimationGroupRegistry.register(modId, enumClass);
    }

    private static void onRegisterAnimationsArmatures(String modId, Class<? extends Enum<?>> enumClass) {
        if (!IArmatureType.class.isAssignableFrom(enumClass)) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<E> - Animation Armature] The namespace '{}' attempted to register a class '{}' that is not an instance of IArmatureType; therefore, registration was prevented...",
                    modId, enumClass.getSimpleName()
            );

            return;
        }

        ArmatureTypeRegistry.register(modId, enumClass);
    }
}
