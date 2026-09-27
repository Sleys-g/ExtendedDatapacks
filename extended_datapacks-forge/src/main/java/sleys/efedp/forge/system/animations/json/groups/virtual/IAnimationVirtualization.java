package sleys.efedp.forge.system.animations.json.groups.virtual;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.sl.epicfight.mixins.AnimationManagerAccessor;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import sleys.sl.library.annotations.BruteForceOperation;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IAnimationVirtualization<T extends StaticAnimation> permits IAnimationExternalVirtualization, VirtualActionAnimationGroup, VirtualAttackAnimationGroup, VirtualStaticAnimationGroup {

    IAnimationGroupType groupType();
    ResourceLocation realAnimation();
    ResourceLocation virtualAnimation();

    default AnimationManager.AnimationAccessor<? extends T> getAccessor() {
        return AnimationManager.byKey(virtualAnimation());
    }

    default boolean isValidAccessor() {
        if (this.getAccessor() == null) {
            var error = String.format("""
                      [<I> - Virtual Animation] The existing animation could not be configured or parameterized because its accessor is null...
                      Accessor: %s
                      Real Animation (ID): %s
                      Virtual Animation (ID): %s
                      Group: %s
                     """,
                    this.getAccessor(), this.realAnimation(),
                    this.virtualAnimation(), this.getGroupId()
            );
            VirtualConfigAnimationErrorPool.addError(error);
            return false;
        }

        if (this.getAccessor().get() == null) {
            ExtendedDatapacks.LOGGER.warn(
                    "[<I> - Virtual Animation] The accessor '{}' exists, but its value is null; it will be forced to initialize before being discarded...",
                    this.getAccessor()
            );

            boolean recovered = this.forceAccessorInitialization(this.getAccessor());

            if (!recovered) {
                var error = String.format("""
                    [<I> - Virtual Animation] The existing animation has a valid accessor, but its value is null even after forcing initialization...
                    Therefore, any virtualization and configuration procedures related to it will be prevented.
                    Accessor: %s
                    Real Animation (ID): %s
                    Virtual Animation (ID): %s
                    Group: %s
                   """, this.getAccessor(), this.realAnimation(),
                        this.virtualAnimation(), this.getGroupId()
                );
                ExtendedDatapacks.LOGGER.error(error);
                return false;
            }

            ExtendedDatapacks.LOGGER.info(
                    "[<I> - Virtual Animation] The forced initialization for the accessor '{}' was valid; its value is no longer null...",
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

    default void setProtocol() {
        VirtualAnimationRegistry.manualVirtualizationProtocol(realAnimation(), virtualAnimation());
    }

    default void configProtocol(IAnimationProperties<T> property) {
        if (this.isInvalidAccessor()) return;
        var animation = getAccessor().get();
        property.applyTo(animation);
        this.isSuccessful();
    }

    default void isSuccessful() {
        ExtendedDatapacks.LOGGER.info(
                "[<I> - Virtual Animation] Successfully processed virtual animation config: {}",
                this.getAccessor()
        );
    }

    default String getGroupId() {
        return this.groupType().id();
    }
}
