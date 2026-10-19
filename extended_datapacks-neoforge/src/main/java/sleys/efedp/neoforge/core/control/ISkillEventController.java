package sleys.efedp.neoforge.core.control;

import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.ProcessSides;
import sleys.efedp.neoforge.core.control.operations.EventControlOperation;
import sleys.efedp.neoforge.core.control.operations.EventInvocationOperation;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

@Internal
public sealed interface ISkillEventController<T extends Event> permits GenericSkillEventControl {
    EventControlOperation<T> eventControl();
    EventInvocationOperation eventsOperation();

    default void executeEventPayload(T event, SkillContainer container) {
        eventControl().execute(event, container);
        var patch = container.getExecutor();
        if (PatchPlayerHelper.isValidPatchedPlayer(patch)) eventsOperation().execute(null, patch);
    }

    default boolean isInvalid(Level level, ProcessSides expectedSide, String task) {
        return !expectedSide.validateSideOrLog(level, task, ISkillEventController::invalidSideLog);
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