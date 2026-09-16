package sleys.efedp.neoforge.system.animations.json.properties.playback.types;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers.AirAnimationLoopSpeed;
import sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers.ChargingAnimationSpeed;
import sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers.ConstantAnimationSpeed;
import sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers.IPlaySpeedModifierParams;
import sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers.ChargingLinkedAnimationSpeed;
import sleys.efedp.neoforge.system.animations.json.properties.playback.registry.IPlaySpeedModifierType;

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
