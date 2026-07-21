package sleys.efedp.system.animations.json.properties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.groups.ActionPropertyGroup;
import sleys.efedp.system.animations.json.properties.groups.StaticPropertyGroup;
import yesman.epicfight.api.animation.types.LongHitAnimation;

public record LongHitAnimationProperties(
        StaticPropertyGroup<LongHitAnimation> staticPropertyGroup,
        ActionPropertyGroup<LongHitAnimation> actionPropertyGroup
) implements IAnimationProperty<LongHitAnimation> {

    public static final MapCodec<LongHitAnimationProperties> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            StaticPropertyGroup.<LongHitAnimation>codec().forGetter(LongHitAnimationProperties::staticPropertyGroup),
                            ActionPropertyGroup.<LongHitAnimation>codec().forGetter(LongHitAnimationProperties::actionPropertyGroup)
                    ).apply(instance, LongHitAnimationProperties::new)
            );

    @Override
    public AnimationRegistryType propertyType() {
        return AnimationRegistryType.LONG_HIT;
    }

    @Override
    public void applyTo(LongHitAnimation animation) {
        staticPropertyGroup.applyTo(animation);
        actionPropertyGroup.applyTo(animation);
    }
}
