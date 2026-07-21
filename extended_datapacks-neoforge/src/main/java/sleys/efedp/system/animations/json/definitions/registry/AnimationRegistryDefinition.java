package sleys.efedp.system.animations.json.definitions.registry;

import com.mojang.serialization.Codec;
import sleys.efedp.system.animations.json.accessor.IAnimationAccessor;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public record AnimationRegistryDefinition<T extends DynamicAnimation>(IAnimationAccessor<T> accessor,
                                                                      IAnimationProperty<T> properties) {

    public static final Codec<AnimationRegistryDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.accessor().accessorType().name().toLowerCase(),
                    type -> AnimationRegistryDefinitionCodec.CODECS
                            .get(AnimationRegistryType.valueOf(type.toUpperCase()))
                            .combined()
            );
}