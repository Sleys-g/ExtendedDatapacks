package sleys.efedp.system.animations.json.properties.functional.playback;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.playback.lambda.*;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public enum PlaySpeedModifierLambdaList {
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

    public <T extends DynamicAnimation> float applyModifiers(
            IPlaySpeedModifierParams lambdas,
            T self, LivingEntityPatch<?> livingEntityPatch,
            float speed, float prevElapsedTime, float elapsedTime) {

        return lambdas.modify(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime);
    }

    public static final Codec<PlaySpeedModifierLambdaList> CODEC = Codec.STRING.flatXmap(
            name -> {
                try {
                    return DataResult.success(PlaySpeedModifierLambdaList.valueOf(name.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown Play Speed Modifier Lambda Type: " + name);
                }
            },
            side -> DataResult.success(side.name())
    );
}
