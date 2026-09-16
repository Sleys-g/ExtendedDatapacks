package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.HitAnimation;

public record HitAnimationProperties(
        StaticPropertyGroup<HitAnimation> staticPropertyGroup
) implements IAnimationProperties<HitAnimation> {

    public static final MapCodec<HitAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<HitAnimation>codec().forGetter(HitAnimationProperties::staticPropertyGroup)
                    ).apply(instance, HitAnimationProperties::new)
            );

    @Override
    public void applyTo(HitAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
