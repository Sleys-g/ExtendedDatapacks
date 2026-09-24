package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;
import sleys.efedp.neoforge.system.animations.json.properties.coords.AnimationCoord;
import sleys.sl.library.util.data.codec.SLCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.utils.TimePairList;

import java.util.Optional;

public record ActionAnimationPropertyGroup<T extends ActionAnimation>(
        Optional<Boolean> stopMovement,
        Optional<Boolean> removeDeltaMovement,
        Optional<Boolean> moveVertical,
        Optional<Boolean> moveOnLink,
        Optional<Boolean> affectSpeed,
        Optional<Boolean> cancelableMove,
        Optional<Boolean> isDeathAnimation,
        Optional<Boolean> resetPlayerComboCounter,
        Optional<Boolean> syncCamera,

        Optional<Vec2> noGravityTime,
        Optional<AnimationCoord<T>> animationCoords
) {

    public static <T extends ActionAnimation> MapCodec<ActionAnimationPropertyGroup<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("stop_movements")
                                .forGetter(ActionAnimationPropertyGroup::stopMovement),

                        Codec.BOOL.optionalFieldOf("remove_delta_move")
                                .forGetter(ActionAnimationPropertyGroup::removeDeltaMovement),

                        Codec.BOOL.optionalFieldOf("move_vertically")
                                .forGetter(ActionAnimationPropertyGroup::moveVertical),

                        Codec.BOOL.optionalFieldOf("move_during_link")
                                .forGetter(ActionAnimationPropertyGroup::moveOnLink),

                        Codec.BOOL.optionalFieldOf("move_speed_based_distance")
                                .forGetter(ActionAnimationPropertyGroup::affectSpeed),

                        Codec.BOOL.optionalFieldOf("cancellable_movement")
                                .forGetter(ActionAnimationPropertyGroup::cancelableMove),

                        Codec.BOOL.optionalFieldOf("is_death")
                                .forGetter(ActionAnimationPropertyGroup::isDeathAnimation),

                        Codec.BOOL.optionalFieldOf("reset_combo_attack_counter")
                                .forGetter(ActionAnimationPropertyGroup::resetPlayerComboCounter),

                        Codec.BOOL.optionalFieldOf("sync_camera")
                                .forGetter(ActionAnimationPropertyGroup::syncCamera),

                        SLCodec.VEC2_CODEC.optionalFieldOf("no_gravity_time")
                                .forGetter(ActionAnimationPropertyGroup::noGravityTime),

                        AnimationCoord.<T>codec().codec().optionalFieldOf("animation_coords")
                                .forGetter(ActionAnimationPropertyGroup::animationCoords)
                ).apply(instance, ActionAnimationPropertyGroup::new)
        );
    }

    public void applyTo(T animation) {
        stopMovement.ifPresent(stopMovement ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, stopMovement));
        removeDeltaMovement.ifPresent(removeDeltaMovement ->   animation.addProperty(AnimationProperty.ActionAnimationProperty.REMOVE_DELTA_MOVEMENT, removeDeltaMovement));
        moveVertical.ifPresent(moveVertical ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, moveVertical));
        moveOnLink.ifPresent(moveOnLink ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, moveOnLink));
        affectSpeed.ifPresent(affectSpeed ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, affectSpeed));
        cancelableMove.ifPresent(cancelableMove ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, cancelableMove));
        isDeathAnimation.ifPresent(isDeathAnimation ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.IS_DEATH_ANIMATION, isDeathAnimation));
        syncCamera.ifPresent(syncCamera ->  animation.addProperty(AnimationProperty.ActionAnimationProperty.SYNC_CAMERA, syncCamera));
        resetPlayerComboCounter.ifPresent(resetPlayerComboCounter -> animation.addProperty(AnimationProperty.ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER, resetPlayerComboCounter));

        noGravityTime.ifPresent(noGravityTime -> animation.addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(noGravityTime.x, noGravityTime.y)));
        animationCoords.ifPresent(animationCoords -> animationCoords.applyCoords(animation));
    }
}
