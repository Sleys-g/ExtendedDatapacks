package sleys.efedp.system.animations.json.definitions.virtualization;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.*;
import sleys.efedp.system.animations.json.virtual.IVirtualAnimation;
import sleys.efedp.system.animations.json.virtual.VirtualActionAnimationGroup;
import sleys.efedp.system.animations.json.virtual.VirtualAttackAnimationGroup;
import sleys.efedp.system.animations.json.virtual.VirtualStaticAnimationGroup;
import yesman.epicfight.api.animation.types.StaticAnimation;

import java.util.Map;

public record AnimationVirtualDefinitionCodec<T extends StaticAnimation>(
        MapCodec<? extends IVirtualAnimation<T>> virtualCodec,
        MapCodec<? extends IAnimationProperty<T>> propertyCodec) {

    @SuppressWarnings("all")
    public MapCodec<AnimationVirtualDefinition<T>> combined() {
        MapCodec<IVirtualAnimation<T>> castedVirtual = (MapCodec<IVirtualAnimation<T>>) (MapCodec<?>) virtualCodec;
        MapCodec<IAnimationProperty<T>> castedProperty = (MapCodec<IAnimationProperty<T>>) (MapCodec<?>) propertyCodec;

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        castedVirtual.forGetter(AnimationVirtualDefinition::virtual),
                        castedProperty.forGetter(AnimationVirtualDefinition::properties)
                ).apply(instance, AnimationVirtualDefinition::new)
        );
    }

    static final Map<AnimationGroupType, AnimationVirtualDefinitionCodec<?>> CODECS =
            Map.of(
                    AnimationGroupType.STATIC_GROUP,
                    new AnimationVirtualDefinitionCodec<>(
                            VirtualStaticAnimationGroup.CODEC,
                            StaticAnimationProperties.CODEC
                    ),

                    AnimationGroupType.ACTION_GROUP,
                    new AnimationVirtualDefinitionCodec<>(
                            VirtualActionAnimationGroup.CODEC,
                            ActionAnimationProperties.CODEC
                    ),

                    AnimationGroupType.ATTACK_GROUP,
                    new AnimationVirtualDefinitionCodec<>(
                            VirtualAttackAnimationGroup.CODEC,
                            AttackAnimationProperties.CODEC
                    )
            );

}
