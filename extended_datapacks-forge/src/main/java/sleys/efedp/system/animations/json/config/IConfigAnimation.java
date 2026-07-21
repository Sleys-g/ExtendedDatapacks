package sleys.efedp.system.animations.json.config;

import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IConfigAnimation<T extends StaticAnimation> permits ConfigActionAnimationGroup, ConfigAttackAnimationGroup, ConfigStaticAnimationGroup {

    AnimationGroupType virtualGroupType();

    ResourceLocation animation();

    default AnimationManager.AnimationAccessor<? extends T> getAccessor() {
        return AnimationManager.byKey(animation());
    }

    void applyConfig(IAnimationProperty<T> property);
}
