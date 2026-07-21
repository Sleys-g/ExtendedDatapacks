package sleys.efedp.system.animations.json.properties.functional.playback;

import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IPlaySpeedModifier<T extends StaticAnimation> permits PlaySpeedModifier {
    void applySpeedModifier(T animation);
}
