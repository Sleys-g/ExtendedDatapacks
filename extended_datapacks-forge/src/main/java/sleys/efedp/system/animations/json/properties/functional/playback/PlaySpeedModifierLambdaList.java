package sleys.efedp.system.animations.json.properties.functional.playback;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.playback.lambda.*;

public enum PlaySpeedModifierLambdaList implements IPlaySpeedModifierType {
    AIR_LOOP(AirAnimationLoopSpeed.CODEC),
    CONSTANT(ConstantAnimationSpeed.CODEC),
    CHARGING(ChargingAnimationSpeed.CODEC),
    CHARGING_LINKED(ChargingLinkedAnimationSpeed.CODEC)

    ;private final MapCodec<? extends IPlaySpeedModifierParams> codec;

    PlaySpeedModifierLambdaList(MapCodec<? extends IPlaySpeedModifierParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IPlaySpeedModifierParams> paramsCodec() {
        return codec;
    }
}
