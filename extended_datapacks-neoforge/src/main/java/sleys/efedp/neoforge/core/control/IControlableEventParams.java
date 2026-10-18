package sleys.efedp.neoforge.core.control;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.ProcessSides;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Internal
public interface IControlableEventParams {

    <T extends Event> void execute(T event, PlayerPatch<?> patch);

    default boolean isInvalid(Level level, ProcessSides expectedSide, String task) {
        return !expectedSide.validateSideOrLog(level, task, IControlableEventParams::invalidSideLog);
    }

    private static void invalidSideLog(ProcessSides expectedSide, String task) {
        switch (expectedSide) {
            case CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[Controlable Events Functions] The event controller: {}, can only be executed on the client side", task);
            case SERVER -> ExtendedDatapacks.LOGGER.error(
                    "[Controlable Events Functions] The event controller: {}, can only be executed on the server side", task);
            case LOCAL_CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[Controlable Events Functions] The event controller: {}, can only be executed on the logical client side", task);
            case BOTH -> {}
        }
    }
}