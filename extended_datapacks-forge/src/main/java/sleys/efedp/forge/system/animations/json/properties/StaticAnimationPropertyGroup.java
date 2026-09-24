package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.playback.PlaySpeedModifier;
import sleys.efedp.forge.system.animations.json.properties.state.AnimationEntityState;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsInIntervalTimeEvent;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsInPeriodTimeEvent;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsInTimeEvent;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsOnProcessEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.List;
import java.util.Optional;

public record StaticAnimationPropertyGroup<T extends StaticAnimation>(
        Optional<Boolean> noPhysics,
        Optional<Boolean> fixedHeadRotation,
        List<AnimationsOnProcessEvent<T>> onProcessEvents,
        List<AnimationsInTimeEvent<T>> inTimeEvents,
        List<AnimationsInPeriodTimeEvent<T>> inPeriodEvents,
        List<AnimationsInIntervalTimeEvent<T>> inIntervalTimeEvents,
        Optional<PlaySpeedModifier<T>> playSpeedModifier,
        Optional<AnimationEntityState<T>> animationEntityState
) {

    public static <T extends StaticAnimation> MapCodec<StaticAnimationPropertyGroup<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.BOOL.optionalFieldOf("no_physics")
                                .forGetter(StaticAnimationPropertyGroup::noPhysics),

                        Codec.BOOL.optionalFieldOf("fixed_head_rotation")
                                .forGetter(StaticAnimationPropertyGroup::fixedHeadRotation),

                        AnimationsOnProcessEvent.<T>codec()
                                        .listOf().optionalFieldOf("on_process_events", List.of())
                                        .forGetter(StaticAnimationPropertyGroup::onProcessEvents),

                        AnimationsInTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_time_events", List.of())
                                .forGetter(StaticAnimationPropertyGroup::inTimeEvents),

                        AnimationsInPeriodTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_period_time_events", List.of())
                                .forGetter(StaticAnimationPropertyGroup::inPeriodEvents),

                        AnimationsInIntervalTimeEvent.<T>codec()
                                .listOf()
                                .optionalFieldOf("in_interval_time_events", List.of())
                                .forGetter(StaticAnimationPropertyGroup::inIntervalTimeEvents),

                        PlaySpeedModifier.<T>codec()
                                .optionalFieldOf("play_speed_modifier")
                                .forGetter(StaticAnimationPropertyGroup::playSpeedModifier),

                        AnimationEntityState.<T>codec()
                                .codec()
                                .optionalFieldOf("animation_entity_state")
                                .forGetter(StaticAnimationPropertyGroup::animationEntityState)

                ).apply(instance, StaticAnimationPropertyGroup::new)
        );
    }

    public void applyTo(T animation) {
        noPhysics.ifPresent(noPhysics ->  animation.addProperty(AnimationProperty.StaticAnimationProperty.NO_PHYSICS, noPhysics));
        fixedHeadRotation.ifPresent(fixedHeadRotation -> animation.addProperty(AnimationProperty.StaticAnimationProperty.FIXED_HEAD_ROTATION, fixedHeadRotation));
        onProcessEvents.forEach(events -> events.applyTo(animation));
        inTimeEvents.forEach(events -> events.applyTo(animation));
        inPeriodEvents.forEach(events -> events.applyTo(animation));
        inIntervalTimeEvents.forEach(events -> events.applyTo(animation));
        playSpeedModifier.ifPresent(playSpeedModifier ->  playSpeedModifier.applySpeedModifier(animation));
        animationEntityState.ifPresent(animationEntityState -> animationEntityState.applyState(animation));
    }
}