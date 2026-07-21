package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.GuardAnimation;

public record GuardAnimationProperties(
        StaticPropertyGroup<GuardAnimation> staticPropertyGroup
) implements IAnimationProperty<GuardAnimation> {

    public static final MapCodec<GuardAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<GuardAnimation>codec().forGetter(GuardAnimationProperties::staticPropertyGroup)
                    ).apply(instance, GuardAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.GUARD;
    }

    @Override
    public void applyTo(GuardAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}
