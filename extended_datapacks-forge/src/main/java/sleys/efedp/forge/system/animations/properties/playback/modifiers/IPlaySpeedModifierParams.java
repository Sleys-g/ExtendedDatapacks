package sleys.efedp.forge.system.animations.properties.playback.modifiers;

import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IPlaySpeedModifierParams {

   <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                             float speed, float prevElapsedTime, float elapsedTime);
}
