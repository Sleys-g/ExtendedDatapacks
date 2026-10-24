package sleys.efedp.forge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.efedp.forge.core.events.list.summon.*;
import sleys.efedp.forge.core.events.registry.IEventType;

public enum SummonEvents implements IEventType {
    THUNDER(SummonThunderEvent.CODEC),
    SHOOT_PROJECTILE(JointShootProjectileEvent.CODEC), /// BETA
    SUMMON_OWNED_ENTITY_ON_TARGET(SummonOwnedEntityOnTargetEvent.CODEC), /// BETA
    SUMMON_ENTITY_ON_TARGET(SummonEntityOnTargetEvent.CODEC), /// BETA
    SUMMON_OWNED_WITHER_GHOST(SummonOwnedWitherGhostEvent.CODEC)

    ;private final MapCodec<? extends IEventParams> codec;

    SummonEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}
