package sleys.efedp.forge.system.animations.json.definitions;

import com.mojang.serialization.Codec;
import sleys.efedp.forge.system.animations.json.animations.accessor.IAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.registry.AnimationAccessorRegistry;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.DynamicAnimation;

public record AnimationAccessorDefinition<T extends DynamicAnimation>(IAnimationAccessor<T> accessor,
                                                                      IAnimationProperties<T> properties) {

    public static final Codec<AnimationAccessorDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.accessor().getAccessorId().toLowerCase(),
                    type -> AnimationAccessorRegistry.get(type)
                            .definitionCodec()
                            .combinedLegacy()
            );
}