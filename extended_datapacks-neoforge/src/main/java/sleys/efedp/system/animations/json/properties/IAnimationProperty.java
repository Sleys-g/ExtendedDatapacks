package sleys.efedp.system.animations.json.properties;

import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationProperty<T extends DynamicAnimation> permits ActionAnimationProperties, AimAnimationProperties, AirAttackAnimationProperties, AttackAnimationProperties, ComboAttackAnimationProperties, DashAttackAnimationProperties, DodgeAnimationProperties, GuardAnimationProperties, HitAnimationProperties, KnockdownAnimationProperties, LongHitAnimationProperties, MovementAnimationProperties, StaticAnimationProperties {

    AnimationRegistryType propertyType();
    void applyTo(T animation);
}
