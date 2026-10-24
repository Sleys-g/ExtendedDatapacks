package sleys.efedp.forge.system.animations.properties.state;

import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IAnimationEntityState<T extends StaticAnimation> permits AnimationEntityState {

    void applyState(T animation);
}
