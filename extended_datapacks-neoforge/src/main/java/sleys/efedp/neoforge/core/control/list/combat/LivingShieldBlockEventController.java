package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingShieldBlockEventController(Optional<Boolean> cancelEvent,
                                               List<EventInvocation> asDamageVictim) implements IControlableEventParams<LivingShieldBlockEvent> {

    public static final MapCodec<LivingShieldBlockEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingShieldBlockEventController::cancelEvent),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_victim", List.of())
                                    .forGetter(LivingShieldBlockEventController::asDamageVictim)
                    ).apply(instance, LivingShieldBlockEventController::new)
            );

    @Override
    public void applyEventParams(LivingShieldBlockEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        if (!(event.getDamageSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageVictim.forEach(invocations -> invocations.execute(dealerEntity));
    }
}