package sleys.efedp.neoforge.system.animations.json.groups.virtual;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
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
        return true;
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
