package sleys.efedp.neoforge.system.animations.properties.state;

import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IAnimationEntityState<T extends StaticAnimation> permits AnimationEntityState {

    void applyState(T animation);
}
