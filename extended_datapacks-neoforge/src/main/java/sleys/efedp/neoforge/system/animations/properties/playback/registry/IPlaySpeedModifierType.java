package sleys.efedp.neoforge.system.animations.properties.playback.registry;

import com.mojang.serialization.MapCodec;
import sleys.efedp.neoforge.system.animations.properties.playback.modifiers.IPlaySpeedModifierParams;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Internal
public interface IPlaySpeedModifierType {
    MapCodec<? extends IPlaySpeedModifierParams> paramsCodec();

    default  <T extends DynamicAnimation> float applyModifiers(
            IPlaySpeedModifierParams lambdas,
            T self, LivingEntityPatch<?> livingEntityPatch,
            float speed, float prevElapsedTime, float elapsedTime) {

        return lambdas.modify(self, livingEntityPatch, speed, prevElapsedTime, elapsedTime);
    }
}
