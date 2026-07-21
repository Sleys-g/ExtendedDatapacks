package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.AttackPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;

public record AttackAnimationProperties(
        StaticPropertyGroup<AttackAnimation> staticPropertyGroup,
        ActionPropertyGroup<AttackAnimation> actionPropertyGroup,
        AttackPropertyGroup<AttackAnimation> attackPropertyGroup
) implements IAnimationProperty<AttackAnimation> {

    public static final MapCodec<AttackAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<AttackAnimation>codec().forGetter(AttackAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<AttackAnimation>codec().forGetter(AttackAnimationProperties::actionPropertyGroup),
                            AttackPropertyGroup.codec().forGetter(AttackAnimationProperties::attackPropertyGroup)
                    ).apply(instance, AttackAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.ATTACK;
    }

    @Override
    public void applyTo(AttackAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}
