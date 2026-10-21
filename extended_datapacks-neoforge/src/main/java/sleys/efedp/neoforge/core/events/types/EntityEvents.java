package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.list.entity.*;
import sleys.efedp.neoforge.core.events.registry.IEventType;

public enum EntityEvents implements IEventType {
    TRANSLATE(TranslateEvent.CODEC),
    TELEPORT(TeleportEvent.CODEC),
    FLASH_WHITE_PAIR(FlashWhitePairEvent.CODEC),
    SCAPE_EMERGENCE_PAIR(ScapeEmergencePairEvent.CODEC),
    ADRENALINE_PAIR(AdrenalinePairEvent.CODEC),
    ENTITY_PAIRING(EntityPairingEvent.CODEC)

    ;private final MapCodec<? extends IEventParams> codec;

    EntityEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}
