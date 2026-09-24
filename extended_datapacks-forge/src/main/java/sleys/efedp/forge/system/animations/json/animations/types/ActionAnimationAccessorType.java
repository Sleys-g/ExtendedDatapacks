package sleys.efedp.forge.system.animations.json.animations.types;

import sleys.efedp.forge.system.animations.json.animations.accessor.ActionAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.DodgeAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.KnockdownAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.LongHitAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.forge.system.animations.json.properties.ActionAnimationProperties;
import sleys.efedp.forge.system.animations.json.animations.accessor.InvincibleAnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;

public enum ActionAnimationAccessorType implements IAnimationAccessorType {
    ACTION("epicfight_edp:action",
            new AnimationAccessorDefinitionCodec<ActionAnimation>(ActionAnimationAccessor.CODEC, ActionAnimationProperties.codec())
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
