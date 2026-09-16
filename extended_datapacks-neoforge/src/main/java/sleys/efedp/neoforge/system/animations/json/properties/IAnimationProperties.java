package sleys.efedp.neoforge.system.animations.json.properties;

import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationProperties<T extends DynamicAnimation> permits ActionAnimationProperties, AimAnimationProperties, AirAttackAnimationProperties, AttackAnimationProperties, ComboAttackAnimationProperties, DashAttackAnimationProperties, DodgeAnimationProperties, GuardAnimationProperties, HitAnimationProperties, KnockdownAnimationProperties, LongHitAnimationProperties, MovementAnimationProperties, StaticAnimationProperties, IAnimationExternalProperties {
    void applyTo(T animation);
}
