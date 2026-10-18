package sleys.efedp.neoforge.system.animations.definitions;

import com.mojang.serialization.Codec;
import sleys.efedp.neoforge.system.animations.animations.accessor.IAnimationAccessor;
import sleys.efedp.neoforge.system.animations.animations.registry.AnimationAccessorRegistry;
import sleys.efedp.neoforge.system.animations.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public record AnimationAccessorDefinition<T extends DynamicAnimation>(IAnimationAccessor<T> accessor,
                                                                      IAnimationProperties<T> properties) {

    public static final Codec<AnimationAccessorDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.accessor().getAccessorId().toLowerCase(),
                    type -> AnimationAccessorRegistry.get(type)
                            .definitionCodec()
                            .combined()
            );
}