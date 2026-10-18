package sleys.efedp.forge.system.animations.animations.types;

import sleys.efedp.forge.system.animations.animations.accessor.*;
import sleys.efedp.forge.system.animations.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.forge.system.animations.properties.StaticAnimationProperties;

public enum StaticAnimationAccessorType implements IAnimationAccessorType {
    STATIC("epicfight_edp:static",
            new AnimationAccessorDefinitionCodec<>(StaticAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    ),
    MOVEMENT("epicfight_edp:movement",
            new AnimationAccessorDefinitionCodec<>(MovementAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    ),
    GUARD("epicfight_edp:guard",
            new AnimationAccessorDefinitionCodec<>(GuardAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    ),
    HIT("epicfight_edp:hit",
            new AnimationAccessorDefinitionCodec<>(HitAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    ),
    AIM("epicfight_edp:aim",
            new AnimationAccessorDefinitionCodec<>(AimAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    ),
    EMOTE("epicfight_edp:emote",
            new AnimationAccessorDefinitionCodec<>(EmoteAnimationAccessor.CODEC, StaticAnimationProperties.codec())
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    StaticAnimationAccessorType(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
