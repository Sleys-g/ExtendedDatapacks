package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;

public record ComboAttackAnimationProperties(
        StaticPropertyGroup<ComboAttackAnimation> staticPropertyGroup,
        ActionPropertyGroup<ComboAttackAnimation> actionPropertyGroup,
        AttackPropertyGroup<ComboAttackAnimation> attackPropertyGroup
) implements IAnimationProperty<ComboAttackAnimation> {

    public static final MapCodec<ComboAttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<ComboAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<ComboAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.<ComboAttackAnimation>codec().forGetter(ComboAttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, ComboAttackAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.COMBO_ATTACK;
    }

    @Override
    public void applyTo(ComboAttackAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
