package sleys.efedp.neoforge.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.types.AttackAnimation;

public record AttackAnimationProperties<T extends AttackAnimation>(
        StaticAnimationPropertyGroup<T> staticPropertyGroup,
        ActionAnimationPropertyGroup<T> actionPropertyGroup,
        AttackAnimationPropertyGroup<T> attackPropertyGroup
) implements IAnimationProperties<T> {

    public static <T extends AttackAnimation> MapCodec<AttackAnimationProperties<T>> codec() {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        StaticAnimationPropertyGroup.<T>codec().forGetter(AttackAnimationProperties::staticPropertyGroup),
                        ActionAnimationPropertyGroup.<T>codec().forGetter(AttackAnimationProperties::actionPropertyGroup),
                        AttackAnimationPropertyGroup.<T>codec().forGetter(AttackAnimationProperties::attackPropertyGroup)
                ).apply(instance, AttackAnimationProperties::new)
        );
    }

    @Override
    public void applyTo(T animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
        attackPropertyGroup.applyTo(animation);
    }
}