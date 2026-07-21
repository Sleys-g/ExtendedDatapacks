package sleys.efedp.system.animations.json.properties.groups;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;
import sleys.efedp.system.animations.json.properties.coords.AnimationCoords;
import sleys.sl.library.util.data.SLCodec;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.utils.TimePairList;

import java.util.Optional;

public record ActionPropertyGroup<T extends ActionAnimation>(
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
        Optional<AnimationCoords<T>> animationCoords
) {

    public static <T extends ActionAnimation> MapCodec<ActionPropertyGroup<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("stop_movements")
                                .forGetter(ActionPropertyGroup::stopMovement),

                        Codec.BOOL.optionalFieldOf("remove_delta_move")
                                .forGetter(ActionPropertyGroup::removeDeltaMovement),

                        Codec.BOOL.optionalFieldOf("move_vertically")
                                .forGetter(ActionPropertyGroup::moveVertical),

                        Codec.BOOL.optionalFieldOf("move_during_link")
                                .forGetter(ActionPropertyGroup::moveOnLink),

                        Codec.BOOL.optionalFieldOf("move_speed_based_distance")
                                .forGetter(ActionPropertyGroup::affectSpeed),

                        Codec.BOOL.optionalFieldOf("cancellable_movement")
                                .forGetter(ActionPropertyGroup::cancelableMove),

                        Codec.BOOL.optionalFieldOf("is_death")
                                .forGetter(ActionPropertyGroup::isDeathAnimation),

                        Codec.BOOL.optionalFieldOf("reset_combo_attack_counter")
                                .forGetter(ActionPropertyGroup::resetPlayerComboCounter),

                        Codec.BOOL.optionalFieldOf("sync_camera")
                                .forGetter(ActionPropertyGroup::syncCamera),

                        SLCodec.VEC2_CODEC.optionalFieldOf("no_gravity_time")
                                .forGetter(ActionPropertyGroup::noGravityTime),

                        AnimationCoords.<T>codec().codec().optionalFieldOf("animation_coords")
                                .forGetter(ActionPropertyGroup::animationCoords)
                ).apply(instance, ActionPropertyGroup::new)
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

        noGravityTime.ifPresent(noGravityTime -> animation.addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(noGravityTime.x, noGravityTime.y)));
        animationCoords.ifPresent(animationCoords -> animationCoords.applyCoords(animation));
    }
}
