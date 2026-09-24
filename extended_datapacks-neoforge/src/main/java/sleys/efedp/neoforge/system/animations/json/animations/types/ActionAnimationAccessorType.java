package sleys.efedp.neoforge.system.animations.json.animations.types;

import sleys.efedp.neoforge.system.animations.json.animations.accessor.*;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.properties.ActionAnimationProperties;

public enum ActionAnimationAccessorType implements IAnimationAccessorType {
    ACTION("epicfight_edp:action",
            new AnimationAccessorDefinitionCodec<>(ActionAnimationAccessor.CODEC, ActionAnimationProperties.codec())
    ),
    DODGE("epicfight_edp:dodge",
            new AnimationAccessorDefinitionCodec<>(DodgeAnimationAccessor.CODEC, ActionAnimationProperties.codec())
    ),
    LONG_HIT("epicfight_edp:long_hit",
            new AnimationAccessorDefinitionCodec<>(LongHitAnimationAccessor.CODEC, ActionAnimationProperties.codec())
    ),
    KNOCKDOWN("epicfight_edp:knockdown",
            new AnimationAccessorDefinitionCodec<>(KnockdownAnimationAccessor.CODEC, ActionAnimationProperties.codec())
    ),
    INVINCIBLE("epicfight_edp:invincible",
            new AnimationAccessorDefinitionCodec<>(InvincibleAnimationAccessor.CODEC, ActionAnimationProperties.codec())
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    ActionAnimationAccessorType(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
