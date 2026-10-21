package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingTriggerTotemEventController(Optional<Boolean> cancelEvent,
                                                List<EventInvocation> asFatalDamageTarget) implements IControlableEventParams<LivingUseTotemEvent> {

    public static final MapCodec<LivingTriggerTotemEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingTriggerTotemEventController::cancelEvent),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_fatal_damage_target", List.of())
                                    .forGetter(LivingTriggerTotemEventController::asFatalDamageTarget)
                    ).apply(instance, LivingTriggerTotemEventController::new)
            );

    @Override
    public void applyEventParams(LivingUseTotemEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        asFatalDamageTarget.forEach(invocations -> invocations.execute(event.getEntity()));
    }
}