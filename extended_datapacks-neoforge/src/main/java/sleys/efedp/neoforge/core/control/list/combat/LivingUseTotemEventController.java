package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingUseTotemEventController(Optional<Boolean> cancelEvent,
                                            List<EventInvocation> asFatalDamageDealer) implements IControlableEventParams<LivingUseTotemEvent> {

    public static final MapCodec<LivingUseTotemEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingUseTotemEventController::cancelEvent),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_fatal_damage_dealer", List.of())
                                    .forGetter(LivingUseTotemEventController::asFatalDamageDealer)
                    ).apply(instance, LivingUseTotemEventController::new)
            );

    @Override
    public void applyEventParams(LivingUseTotemEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity entity)) return;
        asFatalDamageDealer.forEach(invocations -> invocations.execute(entity));
    }
}