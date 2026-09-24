package sleys.efedp.forge.system.animations.json.animations.accessor;

import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationAccessor<T extends DynamicAnimation> permits ActionAnimationAccessor, AimAnimationAccessor, AirAttackAnimationAccessor, AttackAnimationAccessor, ComboAttackAccessor, DashAttackAnimationAccessor, DodgeAnimationAccessor, EmoteAnimationAccessor, GuardAnimationAccessor, HitAnimationAccessor, IAnimationExternalAccessor, InvincibleAnimationAccessor, KnockdownAnimationAccessor, LongHitAnimationAccessor, MountAttackAnimationAccessor, MovementAnimationAccessor, RangedAttackAnimationAccessor, StaticAnimationAccessor {

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
