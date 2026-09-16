package sleys.efedp.forge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.forge.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.forge.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.AirSlashAnimation;

public record AirAttackAnimationProperties(
        StaticPropertyGroup<AirSlashAnimation> staticPropertyGroup,
        ActionPropertyGroup<AirSlashAnimation> actionPropertyGroup,
        AttackPropertyGroup<AirSlashAnimation> attackPropertyGroup
) implements IAnimationProperties<AirSlashAnimation> {

    public static final MapCodec<AirAttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<AirSlashAnimation>codec().forGetter(AirAttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<AirSlashAnimation>codec().forGetter(AirAttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.<AirSlashAnimation>codec().forGetter(AirAttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, AirAttackAnimationProperties::new)
            );

    @Override
    public void applyTo(AirSlashAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
