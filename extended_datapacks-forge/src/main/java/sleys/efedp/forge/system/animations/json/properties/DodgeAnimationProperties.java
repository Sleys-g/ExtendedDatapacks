package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.DodgeAnimation;

public record DodgeAnimationProperties(
        StaticPropertyGroup<DodgeAnimation> staticPropertyGroup,
        ActionPropertyGroup<DodgeAnimation> actionPropertyGroup
) implements IAnimationProperties<DodgeAnimation> {

    public static final MapCodec<DodgeAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<DodgeAnimation>codec().forGetter(DodgeAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<DodgeAnimation>codec().forGetter(DodgeAnimationProperties::actionPropertyGroup)
                    ).apply(instance, DodgeAnimationProperties::new)
            );

    @Override
    public void applyTo(DodgeAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}
