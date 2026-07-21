package sleys.efedp.system.animations.json.properties.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Optional;

public record AnimationEntityState<T extends StaticAnimation>(
        Optional<Boolean> turningLocked,
        Optional<Boolean> movementLocked,
        Optional<Boolean> attacking,
        Optional<Boolean> comboAttackDoable,
        Optional<Boolean> skillExecutable,
        Optional<Boolean> canUseItem,
        Optional<Boolean> canSwitchHandItem,
        Optional<Boolean> inaction,
        Optional<Boolean> knockdown,
        Optional<Boolean> lookTarget,
        Optional<Boolean> updateLivingMotion,
        Optional<Integer> hurtLevel,
        Optional<Integer> phaseLevel
) implements IEntityState<T> {

    public static <T extends StaticAnimation> MapCodec<AnimationEntityState<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("turning_locked").forGetter(AnimationEntityState::turningLocked),
                        Codec.BOOL.optionalFieldOf("movement_locked").forGetter(AnimationEntityState::movementLocked),
                        Codec.BOOL.optionalFieldOf("attacking").forGetter(AnimationEntityState::attacking),
                        Codec.BOOL.optionalFieldOf("combo_attack_doable").forGetter(AnimationEntityState::comboAttackDoable),
                        Codec.BOOL.optionalFieldOf("skill_executable").forGetter(AnimationEntityState::skillExecutable),
                        Codec.BOOL.optionalFieldOf("can_use_item").forGetter(AnimationEntityState::canUseItem),
                        Codec.BOOL.optionalFieldOf("can_switch_hand_item").forGetter(AnimationEntityState::canSwitchHandItem),
                        Codec.BOOL.optionalFieldOf("inaction").forGetter(AnimationEntityState::inaction),
                        Codec.BOOL.optionalFieldOf("knockdown").forGetter(AnimationEntityState::knockdown),
                        Codec.BOOL.optionalFieldOf("look_target").forGetter(AnimationEntityState::lookTarget),
                        Codec.BOOL.optionalFieldOf("update_living_motion").forGetter(AnimationEntityState::updateLivingMotion),
                        Codec.INT.optionalFieldOf("hurt_level").forGetter(AnimationEntityState::hurtLevel),
                        Codec.INT.optionalFieldOf("phase_level").forGetter(AnimationEntityState::phaseLevel)
                ).apply(instance, AnimationEntityState::new)
        );
    }

    @Override
    public void applyState(T animation) {
        turningLocked.ifPresent(turningLocked -> animation.addState(EntityState.TURNING_LOCKED, turningLocked));

        movementLocked.ifPresent(movementLocked -> animation.addState(EntityState.MOVEMENT_LOCKED, movementLocked));
        attacking.ifPresent(attacking -> animation.addState(EntityState.ATTACKING, attacking));
        comboAttackDoable.ifPresent(comboAttackDoable -> animation.addState(EntityState.CAN_BASIC_ATTACK, comboAttackDoable));

        skillExecutable.ifPresent(skillExecutable -> animation.addState(EntityState.CAN_SKILL_EXECUTION, skillExecutable));
        canUseItem.ifPresent(canUseItem -> animation.addState(EntityState.CAN_USE_ITEM, canUseItem));
        canSwitchHandItem.ifPresent(canSwitchHandItem -> animation.addState(EntityState.CAN_SWITCH_HAND_ITEM, canSwitchHandItem));

        inaction.ifPresent(inaction -> animation.addState(EntityState.INACTION, inaction));
        knockdown.ifPresent(knockdown -> animation.addState(EntityState.KNOCKDOWN, knockdown));
        lookTarget.ifPresent(lookTarget -> animation.addState(EntityState.LOCKON_ROTATE, lookTarget));

        updateLivingMotion.ifPresent(updateLivingMotion -> animation.addState(EntityState.UPDATE_LIVING_MOTION, updateLivingMotion));
        hurtLevel.ifPresent(hurtLevel -> animation.addState(EntityState.HURT_LEVEL, hurtLevel));
        phaseLevel.ifPresent(phaseLevel -> animation.addState(EntityState.PHASE_LEVEL, phaseLevel));
    }
}
