package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record StaticAnimationProperties<T extends StaticAnimation>(
        StaticAnimationPropertyGroup<T> staticPropertyGroup
) implements IAnimationProperties<T> {

    public static <T extends StaticAnimation> MapCodec<StaticAnimationProperties<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        StaticAnimationPropertyGroup.<T>codec().forGetter(StaticAnimationProperties::staticPropertyGroup)
                ).apply(instance, StaticAnimationProperties::new)
        );
    }

    @Override
    public void applyTo(T animation) {
        staticPropertyGroup.applyTo(animation);
    }
}