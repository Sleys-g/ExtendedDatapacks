package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.HitAnimation;

public record HitAnimationProperties(
        StaticPropertyGroup<HitAnimation> staticPropertyGroup
) implements IAnimationProperty<HitAnimation> {

    public static final MapCodec<HitAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<HitAnimation>codec().forGetter(HitAnimationProperties::staticPropertyGroup)
                    ).apply(instance, HitAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.HIT;
    }

    @Override
    public void applyTo(HitAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
