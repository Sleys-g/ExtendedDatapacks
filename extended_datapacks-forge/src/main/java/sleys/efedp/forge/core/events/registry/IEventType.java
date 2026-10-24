package sleys.efedp.forge.core.events.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.forge.core.events.IEventParams;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Internal
public interface IEventType {
    MapCodec<? extends IEventParams> paramsCodec();

    default <T extends StaticAnimation> void runEvent(
            IEventParams event, AssetAccessor<T> accessor,
            LivingEntityPatch<?> livingEntityPatch) {
        event.execute(accessor, livingEntityPatch);
    }

    default <T extends StaticAnimation> void runEvent(IEventParams event,
                                                      AssetAccessor<T> accessor,
                                                      LivingEntity entity) {
        event.execute(accessor, entity);
    }
}
