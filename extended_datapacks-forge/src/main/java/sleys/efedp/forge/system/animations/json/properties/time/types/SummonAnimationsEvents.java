package sleys.efedp.forge.system.animations.json.properties.time.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.time.events.*;
import sleys.efedp.forge.system.animations.json.properties.time.registry.IAnimationEventType;

public enum SummonAnimationsEvents implements IAnimationEventType {
    THUNDER(ThunderAnimationEvent.CODEC),
    SHOOT_PROJECTILE(JointShootProjectileEvent.CODEC), /// BETA
    SUMMON_OWNED_ENTITY_ON_TARGET(SummonOwnedEntityOnTargetEvent.CODEC), /// BETA
    SUMMON_ENTITY_ON_TARGET(SummonEntityOnTargetEvent.CODEC), /// BETA
    SUMMON_OWNED_WITHER_GHOST(SummonOwnedWitherGhostEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    SummonAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
