package sleys.efedp.system.animations.json.properties.functional.time;

import yesman.epicfight.api.animation.types.DynamicAnimation;

public interface IAnimationEvent<T extends DynamicAnimation> {
    void applyTo(T animation);

    default String isValid(Object key) {
        return key == null ? "Invalid" : "Valid";
    }
}
