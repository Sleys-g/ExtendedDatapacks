package sleys.efedp.system.animations.json.definitions.virtualization;

import com.mojang.serialization.Codec;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.virtual.IVirtualAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationVirtualDefinition<T extends StaticAnimation>(IVirtualAnimation<T> virtual,
                                                                    IAnimationProperty<T> properties) {

    public static final Codec<AnimationVirtualDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.virtual.virtualGroupType().name().toLowerCase(),
                    type -> AnimationVirtualDefinitionCodec.CODECS
                            .get(AnimationGroupType.valueOf(type.toUpperCase()))
                            .combined()
            );
}