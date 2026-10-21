package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingIncomingTakeDamageEventController(Optional<Boolean> cancelEvent,
                                                      Optional<ArithmeticInstructionPair<Float>> staticDamageInstruction,
                                                      Optional<ArithmeticInstructionDataPair> dynamicDamageInstruction,
                                                      List<EventInvocation> asDamageDealer) implements IControlableEventParams<LivingIncomingDamageEvent> {

    public static final MapCodec<LivingIncomingTakeDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingIncomingTakeDamageEventController::cancelEvent),
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT).codec()
                                    .optionalFieldOf("damage_instruction")
                                    .forGetter(LivingIncomingTakeDamageEventController::staticDamageInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("damage_dynamic_instruction")
                                    .forGetter(LivingIncomingTakeDamageEventController::dynamicDamageInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_dealer", List.of())
                                    .forGetter(LivingIncomingTakeDamageEventController::asDamageDealer)
                    ).apply(instance, LivingIncomingTakeDamageEventController::new)
            );

    @Override
    public void applyEventParams(LivingIncomingDamageEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        var entity = container.getExecutor().getOriginal();
        float damage = event.getAmount();

        if (staticDamageInstruction.isPresent()) {
            var pair = staticDamageInstruction.get();
            damage = pair.getResult(damage, pair.amountValue());
        }

        if (dynamicDamageInstruction.isPresent()) {
            damage = dynamicDamageInstruction.get().getResult(damage, entity);
        }

        if (staticDamageInstruction.isPresent() || dynamicDamageInstruction.isPresent()) {
            event.setAmount(damage);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageDealer.forEach(invocations -> invocations.execute(dealerEntity));
    }
}