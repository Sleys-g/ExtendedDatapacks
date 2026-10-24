package sleys.efedp.forge.core.control.list.movement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.sl.epicfight.client.events.EFMovementInputEvent;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.skill.SkillContainer;

public record MovementInputEventController(boolean disableSprint) implements IControlableEventParams<EFMovementInputEvent.InputEvent> {

    public static final MapCodec<MovementInputEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL
                                    .optionalFieldOf("disable_sprint", false)
                                    .forGetter(MovementInputEventController::disableSprint)
                    ).apply(instance, MovementInputEventController::new)
            );
    @Override
    public void applyEventParams(EFMovementInputEvent.InputEvent event, SkillContainer container) {
        if (disableSprint) {
            event.getPlayer().setSprinting(false);
            ((net.minecraft.client.player.LocalPlayer) event.getPlayer()).sprintTriggerTime = -1;
            ControlEngine.setSprintingKeyStateNotDown();
            var input = event.getInput();

            if (container.getExecutor() != null && input != null) {
                float chargeProgress = container.getExecutor().getSkillChargingTicks() / 30.0F;
                float slowFactor = 1.0F - 0.8F * chargeProgress;
                input.forwardImpulse *= slowFactor;
                input.leftImpulse *= slowFactor;

                if (slowFactor < 0.5F) {
                    input.leftImpulse *= 0.7F;
                }
            }
        }
    }
}