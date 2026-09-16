package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.KnockdownAnimation;

public record KnockdownAnimationProperties(
        StaticPropertyGroup<KnockdownAnimation> staticPropertyGroup,
        ActionPropertyGroup<KnockdownAnimation> actionPropertyGroup
) implements IAnimationProperties<KnockdownAnimation> {

    public static final MapCodec<KnockdownAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<KnockdownAnimation>codec().forGetter(KnockdownAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<KnockdownAnimation>codec().forGetter(KnockdownAnimationProperties::actionPropertyGroup)
                    ).apply(instance, KnockdownAnimationProperties::new)
            );

    @Override
    public void applyTo(KnockdownAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}
