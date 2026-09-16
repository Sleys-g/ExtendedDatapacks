package sleys.efedp.forge.system.animations.json.properties.time.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.time.events.IAnimationEventParams;
import sleys.efedp.forge.system.animations.json.properties.time.events.OnTargetEntityEvent;
import sleys.efedp.forge.system.animations.json.properties.time.events.RandomDiceEvent;
import sleys.efedp.forge.system.animations.json.properties.time.registry.IAnimationEventType;

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
