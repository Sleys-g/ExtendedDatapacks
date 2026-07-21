package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.AimAnimation;

public record AimAnimationProperties(
        StaticPropertyGroup<AimAnimation> staticPropertyGroup
) implements IAnimationProperty<AimAnimation>{

    public static final MapCodec<AimAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<AimAnimation>codec()
                            .forGetter(AimAnimationProperties::staticPropertyGroup)
                    ).apply(instance, AimAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.AIM;
    }

    @Override
    public void applyTo(AimAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
