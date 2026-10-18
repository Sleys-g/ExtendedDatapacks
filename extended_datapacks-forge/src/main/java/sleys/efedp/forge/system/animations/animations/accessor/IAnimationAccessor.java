package sleys.efedp.forge.system.animations.animations.accessor;

import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.properties.IAnimationProperties;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;

@Internal
public interface IAnimationAccessor<T extends DynamicAnimation> {

    IAnimationAccessorType accessorType();

    @SuppressWarnings("UnusedReturnValue")
    AnimationManager.AnimationAccessor<T> register(AnimationManager.AnimationBuilder builder, IAnimationProperties<T> property);

    default void isSuccessful(AnimationManager.AnimationAccessor<T> accessor) {
        ExtendedDatapacks.LOGGER.info(
                "[<I> - Animation Accessor] Successfully registered animation '{}' for accessor type '{}'",
                accessor, this.getAccessorId()
        );
    }

    default String getAccessorId() {
        return this.accessorType().id();
    }
}
