package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.DashAttackAnimation;

public record DashAttackAnimationProperties(
        StaticPropertyGroup<DashAttackAnimation> staticPropertyGroup,
        ActionPropertyGroup<DashAttackAnimation> actionPropertyGroup,
        AttackPropertyGroup<DashAttackAnimation> attackPropertyGroup
) implements IAnimationProperty<DashAttackAnimation> {

    public static final MapCodec<DashAttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<DashAttackAnimation>codec().forGetter(DashAttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<DashAttackAnimation>codec().forGetter(DashAttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.<DashAttackAnimation>codec().forGetter(DashAttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, DashAttackAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.DASH_ATTACK;
    }

    @Override
    public void applyTo(DashAttackAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
