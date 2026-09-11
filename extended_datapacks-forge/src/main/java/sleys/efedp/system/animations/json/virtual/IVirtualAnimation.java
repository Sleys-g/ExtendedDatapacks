package sleys.efedp.system.animations.json.virtual;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.ExtendedDatapacks;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.sl.epicfight.util.helper.animation.VirtualAnimationRegistry;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IVirtualAnimation<T extends StaticAnimation> permits
        VirtualActionAnimationGroup, VirtualAttackAnimationGroup, VirtualStaticAnimationGroup {

    AnimationGroupType virtualGroupType();
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
                    this.virtualAnimation(), this.virtualGroupType()
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

    default void configProtocol(IAnimationProperty<T> property) {
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
}
