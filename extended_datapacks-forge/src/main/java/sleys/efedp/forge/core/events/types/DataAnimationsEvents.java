package sleys.efedp.forge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.core.events.IAnimationEventParams;
import sleys.efedp.forge.core.events.list.data.*;
import sleys.efedp.forge.core.events.registry.IAnimationEventType;

public enum DataAnimationsEvents implements IAnimationEventType {
    SYNCED_DATA_WRITE(SyncedDataWriteEvent.CODEC), /// Only Players
    SYNCED_DIRECT_DATA_READ(SyncedDirectDataReadEvent.CODEC), /// Only Players
    SYNCED_BRANCHED_DATA_READ(SyncedBranchedDataReadEvent.CODEC), /// Only Players
    DATA_WRITE(DataWriteEvent.CODEC),
    DIRECT_DATA_READ(DirectDataReadEvent.CODEC),
    BRANCHED_DATA_READ(BranchedDataReadEvent.CODEC),
    ITEM_STRING_DATA_READ(ItemStringDataReadEvent.CODEC),
    ITEM_NUMBER_DATA_READ(ItemNumberDataReadEvent.CODEC),

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    DataAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
