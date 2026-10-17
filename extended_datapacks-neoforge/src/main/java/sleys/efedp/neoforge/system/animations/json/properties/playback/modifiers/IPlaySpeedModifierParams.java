package sleys.efedp.neoforge.system.animations.json.properties.playback.modifiers;

import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Internal
public interface IPlaySpeedModifierParams {

   <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                             float speed, float prevElapsedTime, float elapsedTime);
}
