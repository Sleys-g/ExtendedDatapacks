package sleys.efedp.system.animations.json.properties.functional.time.list;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.IAnimationEventType;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.CommandAnimationEvent;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.IAnimationEventParams;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.InvulnerabilityAnimationEvent;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.PlayAnimationEvent;

public enum GameplayAnimationsEvents implements IAnimationEventType {
    PLAY_ANIMATION(PlayAnimationEvent.CODEC),
    COMMAND_PAYLOAD(CommandAnimationEvent.CODEC),
    INVULNERABILITY(InvulnerabilityAnimationEvent.CODEC)

    ;private final MapCodec<? extends IAnimationEventParams> codec;

    GameplayAnimationsEvents(MapCodec<? extends IAnimationEventParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IAnimationEventParams> paramsCodec() {
        return codec;
    }
}
