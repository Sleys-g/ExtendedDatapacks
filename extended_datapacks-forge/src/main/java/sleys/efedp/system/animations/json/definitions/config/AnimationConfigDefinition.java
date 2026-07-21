package sleys.efedp.system.animations.json.definitions.config;

import com.mojang.serialization.Codec;
import sleys.efedp.system.animations.json.config.IConfigAnimation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationConfigDefinition<T extends StaticAnimation>(IConfigAnimation<T> config,
                                                                   IAnimationProperty<T> properties) {

    public static final Codec<AnimationConfigDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.config.virtualGroupType().name().toLowerCase(),
                    type -> AnimationConfigDefinitionCodec.CODECS
                            .get(AnimationGroupType.valueOf(type.toUpperCase()))
                            .combinedLegacy()
            );
}
