package sleys.efedp.system.animations.json.properties.coords;

import yesman.epicfight.api.animation.types.ActionAnimation;

public interface IAnimationCoord<T extends ActionAnimation> {

    void applyCoords(T animation);
}
