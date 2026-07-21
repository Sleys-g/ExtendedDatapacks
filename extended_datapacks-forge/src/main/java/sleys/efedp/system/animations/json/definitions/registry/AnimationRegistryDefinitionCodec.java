package sleys.efedp.system.animations.json.definitions.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.accessor.*;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.*;
import sleys.sl.library.annotations.LegacyFunction;
import yesman.epicfight.api.animation.types.DynamicAnimation;

import java.util.Map;

public record AnimationRegistryDefinitionCodec<T extends DynamicAnimation>(
        MapCodec<? extends IAnimationAccessor<T>> accessorCodec,
        MapCodec<? extends IAnimationProperty<T>> propertyCodec) {

    @SuppressWarnings("all")
    public MapCodec<AnimationRegistryDefinition<T>> combined() {
        MapCodec<IAnimationAccessor<T>> castedAccessor = (MapCodec<IAnimationAccessor<T>>) (MapCodec<?>) accessorCodec;
        MapCodec<IAnimationProperty<T>> castedProperty = (MapCodec<IAnimationProperty<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedAccessor.forGetter(AnimationRegistryDefinition::accessor),
                        castedProperty.forGetter(AnimationRegistryDefinition::properties)
                ).apply(instance, AnimationRegistryDefinition::new)
        );
    }

    @LegacyFunction(since = "1.21.1")
    public Codec<AnimationRegistryDefinition<T>> combinedLegacy() {
        return combined().codec();
    }

    static final Map<AnimationRegistryType, AnimationRegistryDefinitionCodec<?>> CODECS =
            Map.ofEntries(
                    Map.entry(AnimationRegistryType.STATIC,
                            new AnimationRegistryDefinitionCodec<>(StaticAnimationAccessor.CODEC, StaticAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.MOVEMENT,
                            new AnimationRegistryDefinitionCodec<>(MovementAnimationAccessor.CODEC, MovementAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.ACTION,
                            new AnimationRegistryDefinitionCodec<>(ActionAnimationAccessor.CODEC, ActionAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.ATTACK,
                            new AnimationRegistryDefinitionCodec<>(AttackAnimationAccessor.CODEC, AttackAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.COMBO_ATTACK,
                            new AnimationRegistryDefinitionCodec<>(ComboAttackAccessor.CODEC, ComboAttackAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.DASH_ATTACK,
                            new AnimationRegistryDefinitionCodec<>(DashAttackAnimationAccessor.CODEC, DashAttackAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.AIR_ATTACK,
                            new AnimationRegistryDefinitionCodec<>(AirAttackAnimationAccessor.CODEC, AirAttackAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.AIM,
                            new AnimationRegistryDefinitionCodec<>(AimAnimationAccessor.CODEC, AimAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.DODGE,
                            new AnimationRegistryDefinitionCodec<>(DodgeAnimationAccessor.CODEC, DodgeAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.GUARD,
                            new AnimationRegistryDefinitionCodec<>(GuardAnimationAccessor.CODEC, GuardAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.LONG_HIT,
                            new AnimationRegistryDefinitionCodec<>(LongHitAnimationAccessor.CODEC, LongHitAnimationProperties.CODEC)),
                    Map.entry(AnimationRegistryType.HIT,
                            new AnimationRegistryDefinitionCodec<>(HitAnimationAccessor.CODEC, HitAnimationProperties.CODEC))
            );

}
