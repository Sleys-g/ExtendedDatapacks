package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.neoforge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record ActionAnimationProperties(
        StaticPropertyGroup<ActionAnimation> staticPropertyGroup,
        ActionPropertyGroup<ActionAnimation> actionPropertyGroup
) implements IAnimationProperties<ActionAnimation> {

    public static final MapCodec<ActionAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<ActionAnimation>codec().forGetter(ActionAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.codec().forGetter(ActionAnimationProperties::actionPropertyGroup)
                    ).apply(instance, ActionAnimationProperties::new)
            );

    @Override
    public void applyTo(ActionAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}
