package sleys.efedp.system.animations.json.properties.functional.time.list;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.*;

public enum EntityAnimationsEvents implements IAnimationEventType {
    TRANSLATE(TranslateEvent.CODEC),
    TELEPORT(TeleportEvent.CODEC),
    FLASH_WHITE_PAIR(FlashWhitePairEvent.CODEC),
    SCAPE_EMERGENCE_PAIR(ScapeEmergencePairEvent.CODEC),
    ENTITY_PAIRING(EntityPairingEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    EntityAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
