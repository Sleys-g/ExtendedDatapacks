package sleys.efedp.forge.system.animations.json.properties.playback;

import yesman.epicfight.api.animation.types.StaticAnimation;

public sealed interface IPlaySpeedModifier<T extends StaticAnimation> permits PlaySpeedModifier {
    void applySpeedModifier(T animation);
}
