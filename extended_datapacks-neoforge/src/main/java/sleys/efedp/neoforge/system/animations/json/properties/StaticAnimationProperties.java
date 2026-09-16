package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record StaticAnimationProperties(
        StaticPropertyGroup<StaticAnimation> staticPropertyGroup
) implements IAnimationProperties<StaticAnimation> {

    public static final MapCodec<StaticAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(StaticPropertyGroup.codec()
                            .forGetter(StaticAnimationProperties::staticPropertyGroup)
                    ).apply(instance, StaticAnimationProperties::new)
            );

    @Override
    public void applyTo(StaticAnimation animation) {
        staticPropertyGroup.applyTo(animation);
    }
}