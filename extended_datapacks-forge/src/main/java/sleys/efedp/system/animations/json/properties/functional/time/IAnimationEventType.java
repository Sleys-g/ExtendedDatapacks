package sleys.efedp.system.animations.json.properties.functional.time;

import com.mojang.serialization.MapCodec;
import sleys.efedp.system.animations.json.properties.functional.time.lambda.IAnimationEventParams;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface IAnimationEventType {
    MapCodec<? extends IAnimationEventParams> paramsCodec();

    default <T extends StaticAnimation> void runEvent(
            IAnimationEventParams event, AssetAccessor<T> accessor,
            LivingEntityPatch<?> livingEntityPatch) {
        event.execute(accessor, livingEntityPatch);
    }
}
