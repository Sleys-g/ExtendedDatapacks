package sleys.efedp.forge.system.animations.json.definitions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.groups.config.IAnimationConfig;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.sl.library.annotations.LegacyFunction;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationConfigDefinitionCodec<T extends StaticAnimation>(
        MapCodec<? extends IAnimationConfig<T>> configCodec,
        MapCodec<? extends IAnimationProperties<T>> propertyCodec) {

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public MapCodec<AnimationConfigDefinition<T>> combined() {
        MapCodec<IAnimationConfig<T>> castedConfig = (MapCodec<IAnimationConfig<T>>) (MapCodec<?>) configCodec;
        MapCodec<IAnimationProperties<T>> castedProperty = (MapCodec<IAnimationProperties<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedConfig.forGetter(AnimationConfigDefinition::config),
                        castedProperty.forGetter(AnimationConfigDefinition::properties)
                ).apply(instance, AnimationConfigDefinition::new)
        );
    }

    @LegacyFunction(since = "1.21.1")
    public Codec<AnimationConfigDefinition<T>> combinedLegacy() {
        return combined().codec();
    }
}
