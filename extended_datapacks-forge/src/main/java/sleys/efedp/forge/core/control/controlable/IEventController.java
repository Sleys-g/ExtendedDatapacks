package sleys.efedp.forge.core.control.controlable;

import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;
import sleys.efedp.forge.ExtendedDatapacks;
import sleys.efedp.forge.core.ProcessSides;
import sleys.efedp.forge.core.control.operations.EventControlOperation;
import sleys.efedp.forge.core.control.operations.EventInvocationOperation;
import sleys.sl.epicfight.util.helper.patch.PatchPlayerHelper;
import sleys.sl.library.annotations.Internal;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

@Internal
public interface IEventController<E extends Event> {
    Optional<EventControlOperation<E>> eventControl();
    Optional<EventInvocationOperation> eventsOperation();

    default void executeEventPayload(E event, SkillContainer container) {
        eventControl().ifPresent(controller -> controller.execute(event, container));

        var patch = container.getExecutor();
        if (!PatchPlayerHelper.isValidPatchedPlayer(patch)) return;

        if (patch.isLogicalClient()) eventsOperation().ifPresent(operation -> operation.execute(
                patch.getClientAnimator().baseLayer.animationPlayer.getRealAnimation(), patch
        ));
        else eventsOperation().ifPresent(operation -> operation.execute(
                 patch.getServerAnimator().animationPlayer.getRealAnimation(), patch
        ));
    }

    default boolean isInvalid(Level level, ProcessSides expectedSide, String task) {
        return !expectedSide.validateSideOrLog(level, task, IEventController::invalidSideLog);
    }

    private static void invalidSideLog(ProcessSides expectedSide, String task) {
        switch (expectedSide) {
            case CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Event Controller] The event controller: {}, can only be executed on the client side", task);
            case SERVER -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Event Controller] The event controller: {}, can only be executed on the server side", task);
            case LOCAL_CLIENT -> ExtendedDatapacks.LOGGER.error(
                    "[<I> Event Controller] The event controller: {}, can only be executed on the logical client side", task);
            case BOTH -> {}
        }
    }
}