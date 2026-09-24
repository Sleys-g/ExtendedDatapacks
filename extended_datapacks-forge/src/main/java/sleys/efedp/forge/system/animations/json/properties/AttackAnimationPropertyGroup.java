package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;

import java.util.Optional;

public record AttackAnimationPropertyGroup<T extends AttackAnimation>(
        Optional<Boolean> fixedMoveDistance,
        Optional<Float> attackSpeedFactor,
        Optional<Float> basisAttackSpeed,
        Optional<Integer> extraColliders,
        Optional<Float> reach
) {

    public static <T extends AttackAnimation> MapCodec<AttackAnimationPropertyGroup<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("fixed_move_distance")
                                .forGetter(AttackAnimationPropertyGroup::fixedMoveDistance),

                        Codec.FLOAT.optionalFieldOf("attack_speed_factor")
                                .forGetter(AttackAnimationPropertyGroup::attackSpeedFactor),

                        Codec.FLOAT.optionalFieldOf("basic_attack_speed")
                                .forGetter(AttackAnimationPropertyGroup::basisAttackSpeed),

                        Codec.INT.optionalFieldOf("extra_colliders")
                                .forGetter(AttackAnimationPropertyGroup::extraColliders),

                        Codec.FLOAT.optionalFieldOf("reach")
                                .forGetter(AttackAnimationPropertyGroup::reach)
                ).apply(instance, AttackAnimationPropertyGroup::new)
        );
    }


    public void applyTo(T animation) {
        fixedMoveDistance.ifPresent(fixedMoveDistance ->  animation.addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, fixedMoveDistance));
        attackSpeedFactor.ifPresent(attackSpeedFactor ->   animation.addProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR, attackSpeedFactor));
        basisAttackSpeed.ifPresent(basisAttackSpeed ->   animation.addProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED, basisAttackSpeed));
        extraColliders.ifPresent(extraColliders ->   animation.addProperty(AnimationProperty.AttackAnimationProperty.EXTRA_COLLIDERS, extraColliders));
        reach.ifPresent(reach ->   animation.addProperty(AnimationProperty.AttackAnimationProperty.REACH, reach));
    }
}
