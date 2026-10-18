package sleys.efedp.neoforge.system.animations.properties.time;

import yesman.epicfight.api.animation.types.DynamicAnimation;

public sealed interface IAnimationEvent<T extends DynamicAnimation> permits
        AnimationsInIntervalTimeEvent, AnimationsInPeriodTimeEvent, AnimationsInTimeEvent, AnimationsOnProcessEvent {

    void applyTo(T animation);

    default String isValid(Object key) {
        return key == null ? "Invalid" : "Valid";
    }
}
