package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;

public record ComboAttackAnimationProperties(
        StaticPropertyGroup<BasicAttackAnimation> staticPropertyGroup,
        ActionPropertyGroup<BasicAttackAnimation> actionPropertyGroup,
        AttackPropertyGroup<BasicAttackAnimation> attackPropertyGroup
) implements IAnimationProperty<BasicAttackAnimation> {

    public static final MapCodec<ComboAttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<BasicAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<BasicAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.<BasicAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, ComboAttackAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.COMBO_ATTACK;
    }

    @Override
    public void applyTo(BasicAttackAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
