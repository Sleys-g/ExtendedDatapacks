package sleys.efedp.system.animations.json.properties.groups;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsInIntervalTimeEvent;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsInPeriodTimeEvent;
import sleys.efedp.system.animations.json.properties.functional.time.AnimationsInTimeEvent;
import sleys.efedp.system.animations.json.properties.functional.playback.PlaySpeedModifier;
import sleys.efedp.system.animations.json.properties.state.AnimationEntityState;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;
import java.util.Optional;

public record StaticPropertyGroup<T extends StaticAnimation>(
        Optional<Boolean> noPhysics,
        Optional<Boolean> fixedHeadRotation,
        List<AnimationsInTimeEvent<T>> inTimeEvents,
        List<AnimationsInPeriodTimeEvent<T>> inPeriodEvents,
        List<AnimationsInIntervalTimeEvent<T>> inIntervalTimeEvents,
        Optional<PlaySpeedModifier<T>> playSpeedModifier,
        Optional<AnimationEntityState<T>> animationEntityState
) {

    public static <T extends StaticAnimation> MapCodec<StaticPropertyGroup<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("no_physics")
                                .forGetter(StaticPropertyGroup::noPhysics),

                        Codec.BOOL.optionalFieldOf("fixed_head_rotation")
                                .forGetter(StaticPropertyGroup::fixedHeadRotation),

                        AnimationsInTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_time_events", List.of())
                                .forGetter(StaticPropertyGroup::inTimeEvents),

                        AnimationsInPeriodTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_period_time_events", List.of())
                                .forGetter(StaticPropertyGroup::inPeriodEvents),

                        AnimationsInIntervalTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_interval_time_events", List.of())
                                .forGetter(StaticPropertyGroup::inIntervalTimeEvents),

                        PlaySpeedModifier.<T>codec()
                                .optionalFieldOf("play_speed_modifier")
                                .forGetter(StaticPropertyGroup::playSpeedModifier),

                        AnimationEntityState.<T>codec()
                                .codec()
                                .optionalFieldOf("animation_entity_state")
                                .forGetter(StaticPropertyGroup::animationEntityState)

                ).apply(instance, StaticPropertyGroup::new)
        );
    }

    public void applyTo(T animation) {
        noPhysics.ifPresent(noPhysics ->  animation.addProperty(AnimationProperty.StaticAnimationProperty.NO_PHYSICS, noPhysics));
        fixedHeadRotation.ifPresent(fixedHeadRotation -> animation.addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, fixedHeadRotation));
        inTimeEvents.forEach(events -> events.applyTo(animation));
        inPeriodEvents.forEach(events -> events.applyTo(animation));
        inIntervalTimeEvents.forEach(events -> events.applyTo(animation));
        playSpeedModifier.ifPresent(playSpeedModifier ->  playSpeedModifier.applySpeedModifier(animation));
        animationEntityState.ifPresent(animationEntityState -> animationEntityState.applyState(animation));
    }
}