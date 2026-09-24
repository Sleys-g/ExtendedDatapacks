package sleys.efedp.neoforge.system.animations.json.animations.types;

import sleys.efedp.neoforge.system.animations.json.animations.accessor.*;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.definitions.AnimationAccessorDefinitionCodec;
import sleys.efedp.neoforge.system.animations.json.properties.AttackAnimationProperties;

public enum AttackAnimationAccessorType implements IAnimationAccessorType {
    ATTACK("epicfight_edp:attack",
            new AnimationAccessorDefinitionCodec<>(AttackAnimationAccessor.CODEC, AttackAnimationProperties.codec())
    ),
    COMBO_ATTACK("epicfight_edp:combo_attack",
            new AnimationAccessorDefinitionCodec<>(ComboAttackAccessor.CODEC, AttackAnimationProperties.codec())
    ),
    DASH_ATTACK("epicfight_edp:dash_attack",
            new AnimationAccessorDefinitionCodec<>(DashAttackAnimationAccessor.CODEC, AttackAnimationProperties.codec())
    ),
    AIR_ATTACK("epicfight_edp:air_attack",
            new AnimationAccessorDefinitionCodec<>(AirAttackAnimationAccessor.CODEC, AttackAnimationProperties.codec())
    ),
    MOUNT_ATTACK("epicfight_edp:mount_attack",
            new AnimationAccessorDefinitionCodec<>(MountAttackAnimationAccessor.CODEC, AttackAnimationProperties.codec())
    ),
    RANGED_ATTACK("epicfight_edp:ranged_attack",
            new AnimationAccessorDefinitionCodec<>(RangedAttackAnimationAccessor.CODEC, AttackAnimationProperties.codec())
    );

    private final String definitionId;
    private final AnimationAccessorDefinitionCodec<?> definitionCodec;

    AttackAnimationAccessorType(String definitionId, AnimationAccessorDefinitionCodec<?> codec) {
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
