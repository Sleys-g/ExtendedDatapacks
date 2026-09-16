package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.GuardAnimation;

public record GuardAnimationProperties(
        StaticPropertyGroup<GuardAnimation> staticPropertyGroup
) implements IAnimationProperties<GuardAnimation> {

    public static final MapCodec<GuardAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<GuardAnimation>codec().forGetter(GuardAnimationProperties::staticPropertyGroup)
                    ).apply(instance, GuardAnimationProperties::new)
            );

    @Override
    public void applyTo(GuardAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
