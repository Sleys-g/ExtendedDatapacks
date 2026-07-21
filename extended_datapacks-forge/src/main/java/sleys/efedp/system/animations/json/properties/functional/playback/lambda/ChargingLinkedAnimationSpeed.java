package sleys.efedp.system.animations.json.properties.functional.playback.lambda;

import com.mojang.serialization.MapCodec;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record ChargingLinkedAnimationSpeed() implements IPlaySpeedModifierParams {

    public static final MapCodec<ChargingLinkedAnimationSpeed> CODEC = MapCodec.unit(ChargingLinkedAnimationSpeed::new);

    @Override
    public <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                                     float speed, float prevElapsedTime, float elapsedTime) {
        return self.isLinkAnimation() ?
                1.0F :
                (float)(-Math.pow((self.getTotalTime() - elapsedTime) / self.getTotalTime() - 1.0F, 2.0F)) + 1.0F;
    }
}
