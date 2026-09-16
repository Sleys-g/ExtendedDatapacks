package sleys.efedp.neoforge.system.animations.json.definitions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.IAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.*;

public record AnimationAccessorDefinitionCodec<T extends DynamicAnimation>(
        MapCodec<? extends IAnimationAccessor<T>> accessorCodec,
        MapCodec<? extends IAnimationProperties<T>> propertyCodec) {

    @SuppressWarnings({"unchecked", "RedundantCast"})
    public MapCodec<AnimationAccessorDefinition<T>> combined() {
        MapCodec<IAnimationAccessor<T>> castedAccessor = (MapCodec<IAnimationAccessor<T>>) (MapCodec<?>) accessorCodec;
        MapCodec<IAnimationProperties<T>> castedProperty = (MapCodec<IAnimationProperties<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedAccessor.forGetter(AnimationAccessorDefinition::accessor),
                        castedProperty.forGetter(AnimationAccessorDefinition::properties)
                ).apply(instance, AnimationAccessorDefinition::new)
        );
    }
}