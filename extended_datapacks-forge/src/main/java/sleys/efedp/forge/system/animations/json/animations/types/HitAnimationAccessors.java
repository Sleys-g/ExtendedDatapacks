package sleys.efedp.forge.system.animations.json.animations.types;

import sleys.efedp.forge.system.animations.json.animations.accessor.HitAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.KnockdownAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.LongHitAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.forge.system.animations.json.properties.HitAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.KnockdownAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.LongHitAnimationProperties;

public enum HitAnimationAccessors implements IAnimationAccessorType {
    LONG_HIT("epicfight_edp:long_hit",
            new AnimationAccessorDefinitionCodec<>(LongHitAnimationAccessor.CODEC, LongHitAnimationProperties.CODEC)
    ),
    HIT("epicfight_edp:hit",
            new AnimationAccessorDefinitionCodec<>(HitAnimationAccessor.CODEC, HitAnimationProperties.CODEC)
    ),
    KNOCKDOWN("epicfight_edp:knockdown",
            new AnimationAccessorDefinitionCodec<>(KnockdownAnimationAccessor.CODEC, KnockdownAnimationProperties.CODEC)
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    HitAnimationAccessors(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
        this.definitionId = definitionId;
        this.definitionCodec = codec;
    }

    @Override
    public String id() {
        return definitionId;
    }

    @Override
    public AnimationAccessorDefinitionCodec<?> definitionCodec() {
        return definitionCodec;
    }
}
