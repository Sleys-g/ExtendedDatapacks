package sleys.efedp.forge.system.animations.json.animations.types;

import sleys.efedp.forge.system.animations.json.animations.accessor.ActionAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.DodgeAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.accessor.GuardAnimationAccessor;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.forge.system.animations.json.properties.ActionAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.DodgeAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.GuardAnimationProperties;

public enum InteractionAnimationAccessors implements IAnimationAccessorType {
    ACTION("epicfight_edp:action",
            new AnimationAccessorDefinitionCodec<>(ActionAnimationAccessor.CODEC, ActionAnimationProperties.CODEC)
    ),
    DODGE("epicfight_edp:dodge",
            new AnimationAccessorDefinitionCodec<>(DodgeAnimationAccessor.CODEC, DodgeAnimationProperties.CODEC)
    ),
    GUARD("epicfight_edp:guard",
            new AnimationAccessorDefinitionCodec<>(GuardAnimationAccessor.CODEC, GuardAnimationProperties.CODEC)
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    InteractionAnimationAccessors(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
