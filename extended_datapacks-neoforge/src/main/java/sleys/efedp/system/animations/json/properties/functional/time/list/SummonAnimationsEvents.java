package sleys.efedp.system.animations.json.properties.functional.time.list;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;

public enum SummonAnimationsEvents implements IAnimationEventType {
    THUNDER(ThunderAnimationEvent.CODEC),
    SHOOT_PROJECTILE(JointShootProjectileEvent.CODEC), /// BETA
    SUMMON_OWNED_ENTITY_ON_TARGET(SummonOwnedEntityOnTargetEvent.CODEC), /// BETA
    SUMMON_ENTITY_ON_TARGET(SummonEntityOnTargetEvent.CODEC), /// BETA

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    SummonAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
