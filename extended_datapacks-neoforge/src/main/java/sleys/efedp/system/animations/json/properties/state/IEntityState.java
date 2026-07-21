package sleys.efedp.system.animations.json.properties.state;

import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IEntityState<T extends StaticAnimation> permits AnimationEntityState {

    void applyState(T animation);
}
