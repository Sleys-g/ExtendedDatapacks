package sleys.efedp.system.animations.json.accessor;

import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationAccessor<T extends DynamicAnimation> permits ActionAnimationAccessor, AimAnimationAccessor, AirAttackAnimationAccessor, AttackAnimationAccessor, ComboAttackAccessor, DashAttackAnimationAccessor, DodgeAnimationAccessor, GuardAnimationAccessor, HitAnimationAccessor, KnockdownAnimationAccessor, LongHitAnimationAccessor, MovementAnimationAccessor, StaticAnimationAccessor {

    AnimationRegistryType accessorType();

    AnimationManager.AnimationAccessor<T> register(AnimationManager.AnimationBuilder builder, IAnimationProperty<T> properties);
}
