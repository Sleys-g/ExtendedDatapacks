package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record StaticAnimationProperties(
        StaticPropertyGroup<StaticAnimation> staticPropertyGroup
) implements IAnimationProperty<StaticAnimation> {

    public static final MapCodec<StaticAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.codec()
                            .forGetter(StaticAnimationProperties::staticPropertyGroup)
                    ).apply(instance, StaticAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.STATIC;
    }

    @Override
    public void applyTo(StaticAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}