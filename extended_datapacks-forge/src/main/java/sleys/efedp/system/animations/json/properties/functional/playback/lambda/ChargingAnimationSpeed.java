package sleys.efedp.system.animations.json.properties.functional.playback.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import yesman.epicfight.api.animation.SynchedAnimationVariableKeys;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record ChargingAnimationSpeed(Float maxElapse) implements IPlaySpeedModifierParams {

    public static final MapCodec<ChargingAnimationSpeed> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("max_elapse").forGetter(ChargingAnimationSpeed::maxElapse)
            ).apply(instance, ChargingAnimationSpeed::new)
    );

    @Override
    public <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                                     float speed, float prevElapsedTime, float elapsedTime) {
        if (elapsedTime < maxElapse) {
            int chargingPower = patch.getAnimator().getVariables().get(
                    SynchedAnimationVariableKeys.CHARGING_TICKS.get(),
                    self.getRealAnimation()).orElse(0
            );
            return 0.6666F + (float)chargingPower / 20.0F;
        }

        return 1.0F;
    }
}
