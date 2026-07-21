package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.MovementAnimation;

public record MovementAnimationProperties(
        StaticPropertyGroup<MovementAnimation> staticPropertyGroup
) implements IAnimationProperty<MovementAnimation> {

    public static final MapCodec<MovementAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.<MovementAnimation>codec()
                            .forGetter(MovementAnimationProperties::staticPropertyGroup)
                    ).apply(instance, MovementAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.MOVEMENT;
    }

    @Override
    public void applyTo(MovementAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}