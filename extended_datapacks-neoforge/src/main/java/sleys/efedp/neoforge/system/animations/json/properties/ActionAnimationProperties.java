package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record ActionAnimationProperties<T extends ActionAnimation>(
        StaticAnimationPropertyGroup<T> staticPropertyGroup,
        ActionAnimationPropertyGroup<T> actionPropertyGroup
) implements IAnimationProperties<T> {

    public static <T extends ActionAnimation> MapCodec<ActionAnimationProperties<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        StaticAnimationPropertyGroup.<T>codec().forGetter(ActionAnimationProperties::staticPropertyGroup),
                        ActionAnimationPropertyGroup.<T>codec().forGetter(ActionAnimationProperties::actionPropertyGroup)
                ).apply(instance, ActionAnimationProperties::new)
        );
    }

    @Override
    public void applyTo(T animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}