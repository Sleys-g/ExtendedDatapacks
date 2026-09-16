package sleys.efedp.neoforge.system.animations.json.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.groups.virtual.IAnimationVirtualization;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record AnimationVirtualizationDefinitionCodec<T extends StaticAnimation>(
        MapCodec<? extends IAnimationVirtualization<T>> virtualCodec,
        MapCodec<? extends IAnimationProperties<T>> propertyCodec) {

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public MapCodec<AnimationVirtualizationDefinition<T>> combined() {
        MapCodec<IAnimationVirtualization<T>> castedVirtual = (MapCodec<IAnimationVirtualization<T>>) (MapCodec<?>) virtualCodec;
        MapCodec<IAnimationProperties<T>> castedProperty = (MapCodec<IAnimationProperties<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedVirtual.forGetter(AnimationVirtualizationDefinition::virtual),
                        castedProperty.forGetter(AnimationVirtualizationDefinition::properties)
                ).apply(instance, AnimationVirtualizationDefinition::new)
        );
    }
}
