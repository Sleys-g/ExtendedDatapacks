package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.AimAnimation;

public record AimAnimationProperties(
        StaticPropertyGroup<AimAnimation> staticPropertyGroup
) implements IAnimationProperties<AimAnimation> {

    public static final MapCodec<AimAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<AimAnimation>codec()
                            .forGetter(AimAnimationProperties::staticPropertyGroup)
                    ).apply(instance, AimAnimationProperties::new)
            );

    @Override
    public void applyTo(AimAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
