package sleys.efedp.system.animations.json.virtual;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.sl.epicfight.helper.animation.VirtualAnimationRegistry;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IVirtualAnimation<T extends StaticAnimation> permits
        VirtualActionAnimationGroup, VirtualAttackAnimationGroup, VirtualStaticAnimationGroup {

    AnimationGroupType virtualGroupType();
    ResourceLocation realAnimation();
    ResourceLocation virtualAnimation();

    default AnimationManager.AnimationAccessor<? extends T> getAnimation() {
        return AnimationManager.byKey(virtualAnimation());
    }

    /// Called in "Registry Event"
    default void setProtocol() {
        VirtualAnimationRegistry.manualVirtualizationProtocol(realAnimation(), virtualAnimation());
    }

    default void configProtocol(IAnimationProperty<T> property) {
        var animation = getAnimation().get();
        property.applyTo(animation);
    }
}
