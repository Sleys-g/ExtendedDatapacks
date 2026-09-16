package sleys.efedp.neoforge.system.animations.json.animations.types;

import sleys.efedp.neoforge.system.animations.json.animations.accessor.AimAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.AirAttackAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.ComboAttackAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.DashAttackAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.accessor.AttackAnimationAccessor;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.properties.AimAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.AirAttackAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.AttackAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.DashAttackAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.ComboAttackAnimationProperties;

public enum CombatAnimationAccessors implements IAnimationAccessorType {
    ATTACK("epicfight_edp:attack",
            new AnimationAccessorDefinitionCodec<>(AttackAnimationAccessor.CODEC, AttackAnimationProperties.CODEC)
    ),
    COMBO_ATTACK("epicfight_edp:combo_attack",
            new AnimationAccessorDefinitionCodec<>(ComboAttackAccessor.CODEC, ComboAttackAnimationProperties.CODEC)
    ),
    DASH_ATTACK("epicfight_edp:dash_attack",
            new AnimationAccessorDefinitionCodec<>(DashAttackAnimationAccessor.CODEC, DashAttackAnimationProperties.CODEC)
    ),
    AIR_ATTACK("epicfight_edp:air_attack",
            new AnimationAccessorDefinitionCodec<>(AirAttackAnimationAccessor.CODEC, AirAttackAnimationProperties.CODEC)
    ),
    AIM("epicfight_edp:aim",
            new AnimationAccessorDefinitionCodec<>(AimAnimationAccessor.CODEC, AimAnimationProperties.CODEC)
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    CombatAnimationAccessors(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
