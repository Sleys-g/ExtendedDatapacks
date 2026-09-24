package sleys.efedp.forge.system.animations.json.properties;

import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationProperties<T extends DynamicAnimation> permits
        ActionAnimationProperties, AttackAnimationProperties, IAnimationExternalProperties, StaticAnimationProperties {
    void applyTo(T animation);
}
