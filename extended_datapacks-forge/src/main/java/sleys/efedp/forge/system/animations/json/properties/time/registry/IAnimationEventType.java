package sleys.efedp.forge.system.animations.json.properties.time.registry;

import com.mojang.serialization.MapCodec;
import sleys.efedp.forge.system.animations.json.properties.time.events.IAnimationEventParams;
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
