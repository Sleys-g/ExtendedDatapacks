package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.efedp.neoforge.core.events.list.game.OnTargetEntityEvent;
import sleys.efedp.neoforge.core.events.list.game.RandomDiceEvent;
import sleys.efedp.neoforge.core.events.registry.IEventType;

public enum GameEvents implements IEventType {
    RANDOM_DICE(RandomDiceEvent.CODEC),
    ON_TARGET_ENTITY(OnTargetEntityEvent.CODEC),

    ;private final MapCodec<? extends IEventParams> codec;

    GameEvents(MapCodec<? extends IEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IEventParams> paramsCodec() {
        return codec;
    }
}
