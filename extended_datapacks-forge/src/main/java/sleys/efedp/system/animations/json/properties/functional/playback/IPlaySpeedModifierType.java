package sleys.efedp.system.animations.json.properties.functional.playback;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.playback.lambda.IPlaySpeedModifierParams;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IPlaySpeedModifierType {
    MapCodec<? extends IPlaySpeedModifierParams> paramsCodec();

    default  <T extends DynamicAnimation> float applyModifiers(
            IPlaySpeedModifierParams lambdas,
            T self, LivingEntityPatch<?> livingEntityPatch,
            float speed, float prevElapsedTime, float elapsedTime) {

        return lambdas.modify(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime);
    }
}
