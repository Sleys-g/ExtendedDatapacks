package sleys.efedp.neoforge.system.animations.json.properties;

import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.DynamicAnimation;

@Internal
public interface IAnimationProperties<T extends DynamicAnimation> {
    void applyTo(T animation);
}
