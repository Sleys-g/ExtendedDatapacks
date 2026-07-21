package sleys.efedp.system.animations.json.properties.functional.playback.lambda;

import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public sealed interface IPlaySpeedModifierParams permits
        AirAnimationLoopSpeed, ChargingAnimationSpeed,
        ChargingLinkedAnimationSpeed, ConstantAnimationSpeed {

   <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                             float speed, float prevElapsedTime, float elapsedTime);
}
