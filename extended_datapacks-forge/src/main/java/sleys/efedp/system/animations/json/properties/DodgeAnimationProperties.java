package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.DodgeAnimation;

public record DodgeAnimationProperties(
        StaticPropertyGroup<DodgeAnimation> staticPropertyGroup,
        ActionPropertyGroup<DodgeAnimation> actionPropertyGroup
) implements IAnimationProperty<DodgeAnimation> {

    public static final MapCodec<DodgeAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<DodgeAnimation>codec().forGetter(DodgeAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<DodgeAnimation>codec().forGetter(DodgeAnimationProperties::actionPropertyGroup)
                    ).apply(instance, DodgeAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.DODGE;
    }

    @Override
    public void applyTo(DodgeAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}
