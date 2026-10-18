package sleys.efedp.forge.core.events;


import net.minecraft.world.level.Level;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.core.ProcessSides;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Internal
public interface IAnimationEventParams {

    <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch);

    default boolean isInvalid(Level level, ProcessSides expectedSide, String task) {
        return !expectedSide.validateSideOrLog(level, task, IAnimationEventParams::invalidSideLog);
    }

    private static void invalidSideLog(ProcessSides expectedSide, String task) {
        switch (expectedSide) {
            case CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[Animation Events Functions] The task: {}, can only be executed on the client side", task);
            case SERVER -> ExtendedDatapacks.LOGGER.error(
                    "[Animation Events Functions] The task: {}, can only be executed on the server side", task);
            case LOCAL_CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[Animation Events Functions] The task: {}, can only be executed on the logical client side", task);
            case BOTH -> {}
        }
    }
}