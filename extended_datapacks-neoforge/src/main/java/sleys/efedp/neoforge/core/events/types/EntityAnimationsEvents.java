package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IAnimationEventParams;
import sleys.efedp.neoforge.core.events.list.entity.*;
import sleys.efedp.neoforge.core.events.registry.IAnimationEventType;

public enum EntityAnimationsEvents implements IAnimationEventType {
    TRANSLATE(TranslateEvent.CODEC),
    TELEPORT(TeleportEvent.CODEC),
    FLASH_WHITE_PAIR(FlashWhitePairEvent.CODEC),
    SCAPE_EMERGENCE_PAIR(ScapeEmergencePairEvent.CODEC),
    ADRENALINE_PAIR(AdrenalinePairEvent.CODEC),
    ENTITY_PAIRING(EntityPairingEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    EntityAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
