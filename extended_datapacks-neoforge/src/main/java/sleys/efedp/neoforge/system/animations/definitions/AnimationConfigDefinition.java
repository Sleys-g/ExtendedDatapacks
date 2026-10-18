package sleys.efedp.neoforge.system.animations.definitions;

import com.mojang.serialization.Codec;
import sleys.efedp.neoforge.system.animations.groups.config.IAnimationConfig;
import sleys.efedp.neoforge.system.animations.groups.registry.AnimationGroupRegistry;
import sleys.efedp.neoforge.system.animations.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationConfigDefinition<T extends StaticAnimation>(IAnimationConfig<T> config,
                                                                   IAnimationProperties<T> properties) {

    public static final Codec<AnimationConfigDefinition<?>> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    def -> def.config().getGroupId().toLowerCase(),
                    type -> AnimationGroupRegistry.get(type)
                            .configDefinitionCodec()
                            .combined()
            );
}
