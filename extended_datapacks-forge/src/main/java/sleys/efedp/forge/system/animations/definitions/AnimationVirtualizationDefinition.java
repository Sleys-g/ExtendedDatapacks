package sleys.efedp.forge.system.animations.definitions;

import com.mojang.serialization.Codec;
import sleys.efedp.forge.system.animations.groups.registry.AnimationGroupRegistry;
import sleys.efedp.forge.system.animations.groups.virtual.IAnimationVirtualization;
import sleys.efedp.forge.system.animations.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationVirtualizationDefinition<T extends StaticAnimation>(IAnimationVirtualization<T> virtual,
                                                                           IAnimationProperties<T> properties) {

    public static final Codec<AnimationVirtualizationDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.virtual().getGroupId().toLowerCase(),
                    type -> AnimationGroupRegistry.get(type)
                            .virtualDefinitionCodec()
                            .combinedLegacy()
            );
}