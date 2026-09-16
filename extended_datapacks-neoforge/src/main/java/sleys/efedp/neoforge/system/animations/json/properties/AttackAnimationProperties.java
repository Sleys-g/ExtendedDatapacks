package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.neoforge.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.neoforge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.AttackAnimation;

public record AttackAnimationProperties(
        StaticPropertyGroup<AttackAnimation> staticPropertyGroup,
        ActionPropertyGroup<AttackAnimation> actionPropertyGroup,
        AttackPropertyGroup<AttackAnimation> attackPropertyGroup
) implements IAnimationProperties<AttackAnimation> {

    public static final MapCodec<AttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<AttackAnimation>codec().forGetter(AttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<AttackAnimation>codec().forGetter(AttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.codec().forGetter(AttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, AttackAnimationProperties::new)
            );

    @Override
    public void applyTo(AttackAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
