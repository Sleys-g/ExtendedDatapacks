package sleys.efedp.neoforge.core.events;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.sl.library.annotations.Internal;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Internal
public interface IEventParams {

    <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch);

    default <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        ExtendedDatapacks.LOGGER.warn("[<I> Animation Event Params] Call not overwritten—and therefore not permitted—for accessor '{}', whose patch is null.", accessor);
    }

    default boolean isInvalid(Level level, ProcessSides expectedSide, String task) {
        return !expectedSide.validateSideOrLog(level, task, IEventParams::invalidSideLog);
    }

    private static void invalidSideLog(ProcessSides expectedSide, String task) {
        switch (expectedSide) {
            case CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Animation Event Params] The task: {}, can only be executed on the client side", task);
            case SERVER -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Animation Event Params] The task: {}, can only be executed on the server side", task);
            case LOCAL_CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Animation Event Params] The task: {}, can only be executed on the logical client side", task);
            case BOTH -> {}
        }
    }
}