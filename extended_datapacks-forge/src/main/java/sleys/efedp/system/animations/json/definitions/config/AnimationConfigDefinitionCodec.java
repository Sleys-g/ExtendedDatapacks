package sleys.efedp.system.animations.json.definitions.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.config.ConfigActionAnimationGroup;
import sleys.efedp.system.animations.json.config.ConfigAttackAnimationGroup;
import sleys.efedp.system.animations.json.config.ConfigStaticAnimationGroup;
import sleys.efedp.system.animations.json.config.IConfigAnimation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.ActionAnimationProperties;
import sleys.efedp.system.animations.json.properties.AttackAnimationProperties;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.StaticAnimationProperties;
import sleys.sl.library.annotations.LegacyFunction;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Map;

public record AnimationConfigDefinitionCodec<T extends StaticAnimation>(
        MapCodec<? extends IConfigAnimation<T>> configCodec,
        MapCodec<? extends IAnimationProperty<T>> propertyCodec) {

    @SuppressWarnings("all")
    public MapCodec<AnimationConfigDefinition<T>> combined() {
        MapCodec<IConfigAnimation<T>> castedConfig = (MapCodec<IConfigAnimation<T>>) (MapCodec<?>) configCodec;
        MapCodec<IAnimationProperty<T>> castedProperty = (MapCodec<IAnimationProperty<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedConfig.forGetter(AnimationConfigDefinition::config),
                        castedProperty.forGetter(AnimationConfigDefinition::properties)
                ).apply(instance, AnimationConfigDefinition::new)
        );
    }

    @LegacyFunction(since = "1.21.1")
    public Codec<AnimationConfigDefinition<T>> combinedLegacy() {
        return combined().codec();
    }

    static final Map<AnimationGroupType, AnimationConfigDefinitionCodec<?>> CODECS =
            Map.of(
                    AnimationGroupType.STATIC_GROUP,
                    new AnimationConfigDefinitionCodec<>(
                            ConfigStaticAnimationGroup.CODEC,
                            StaticAnimationProperties.CODEC
                    ),

                    AnimationGroupType.ACTION_GROUP,
                    new AnimationConfigDefinitionCodec<>(
                            ConfigActionAnimationGroup.CODEC,
                            ActionAnimationProperties.CODEC
                    ),

                    AnimationGroupType.ATTACK_GROUP,
                    new AnimationConfigDefinitionCodec<>(
                            ConfigAttackAnimationGroup.CODEC,
                            AttackAnimationProperties.CODEC
                    )
            );

}
