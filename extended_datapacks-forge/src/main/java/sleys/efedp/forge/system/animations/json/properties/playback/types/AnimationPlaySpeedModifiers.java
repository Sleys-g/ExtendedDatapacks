package sleys.efedp.forge.system.animations.json.properties.playback.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.playback.modifiers.*;
import sleys.efedp.forge.system.animations.json.properties.playback.registry.IPlaySpeedModifierType;

public enum AnimationPlaySpeedModifiers implements IPlaySpeedModifierType {
    AIR_LOOP(AirAnimationLoopSpeed.CODEC),
    CONSTANT(ConstantAnimationSpeed.CODEC),
    CHARGING(ChargingAnimationSpeed.CODEC),
    CHARGING_LINKED(ChargingLinkedAnimationSpeed.CODEC)

    ;private final MapCodec<? extends IPlaySpeedModifierParams> codec;

    AnimationPlaySpeedModifiers(MapCodec<? extends IPlaySpeedModifierParams> codec) {
        this.codec = codec;
    }

    public MapCodec<? extends IPlaySpeedModifierParams> paramsCodec() {
        return codec;
    }
}
