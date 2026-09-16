package sleys.efedp.neoforge.system.animations.json.animations.types;

import sleys.efedp.neoforge.system.animations.json.animations.accessor.StaticAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.MovementAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.properties.MovementAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.StaticAnimationProperties;

public enum LivingAnimationAccessors implements IAnimationAccessorType {
    STATIC("epicfight_edp:static",
            new AnimationAccessorDefinitionCodec<>(StaticAnimationAccessor.CODEC, StaticAnimationProperties.CODEC)
    ),
    MOVEMENT("epicfight_edp:movement",
            new AnimationAccessorDefinitionCodec<>(MovementAnimationAccessor.CODEC, MovementAnimationProperties.CODEC)
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    LivingAnimationAccessors(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
