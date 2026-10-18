package sleys.efedp.neoforge.core.events.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.core.events.IAnimationEventParams;
import sleys.efedp.neoforge.core.events.list.game.OnTargetEntityEvent;
import sleys.efedp.neoforge.core.events.list.game.RandomDiceEvent;
import sleys.efedp.neoforge.core.events.registry.IAnimationEventType;

public enum GameAnimationsEvents implements IAnimationEventType {
    RANDOM_DICE(RandomDiceEvent.CODEC),
    ON_TARGET_ENTITY(OnTargetEntityEvent.CODEC),

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    GameAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    @Override
    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
