package sleys.efedp.neoforge.system.animations.json.groups.config;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.sl.epicfight.mixins.AnimationManagerAccessor;
import sleys.sl.library.annotations.BruteForceOperation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IAnimationConfig<T extends StaticAnimation> permits ConfigActionAnimationGroup, ConfigAttackAnimationGroup, ConfigStaticAnimationGroup, IAnimationExternalConfig {

    IAnimationGroupType groupType();
    ResourceLocation animation();

    default AnimationManager.AnimationAccessor<? extends T> getAccessor() {
        return AnimationManager.byKey(animation());
    }

    default boolean isValidAccessor() {
        if (this.getAccessor() == null) {
            var error = String.format("""
                    [<I> - Config Animation] The existing animation could not be configured or parameterized because its accessor is null...
                    Accessor: %s
                    Animation (ID): %s
                    Group: %s
                   """, this.getAccessor(), this.animation(), this.getGroupId()
            );
            ConfigAnimationsErrorPool.addError(error);
            return false;
        }

        if (this.getAccessor().get() == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<I> - Config Animation] The accessor '{}' exists, but its value is null; it will be forced to initialize before being discarded...",
                    this.getAccessor()
            );

            boolean recovered = this.forceAccessorInitialization(this.getAccessor());

            if (!recovered) {
                var error = String.format("""
                    [<I> - Config Animation] The existing animation has a valid accessor, but its value is null even after forcing initialization...
                    Therefore, any configuration procedures related to it will be prevented.
                    Accessor: %s
                    Animation (ID): %s
                    Group: %s
                   """, this.getAccessor(), this.animation(), this.getGroupId()
                );
                ExtendedDatapacks.LOGGER.error(error);
                return false;
            }

            ExtendedDatapacks.LOGGER.info(
                    "[<I> - Config Animation] The forced initialization for the accessor '{}' was valid; its value is no longer null...",
                    this.getAccessor()
            );
        }

        return true;
    }

    @BruteForceOperation(overwrites = "AnimationAccessorImpl[onLoad]")
    private boolean forceAccessorInitialization(AnimationManager.AnimationAccessor<?> accessor) {
        var manager = (AnimationManagerAccessor) AnimationManager.getInstance();
        manager.getAnimations().remove(accessor);
        var forced = accessor.get();
        return forced != null;
    }

    default boolean isInvalidAccessor() {
        return !isValidAccessor();
    }

    void applyConfig(IAnimationProperties<T> property);

    default void isSuccessful() {
        ExtendedDatapacks.LOGGER.info(
                "[<I> - Config Animation] Successfully processed animation config: {}",
                this.getAccessor()
        );
    }

    default String getGroupId() {
        return this.groupType().id();
    }
}
