package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingIncomingDealDamageEventController(Optional<ArithmeticInstructionPair<Float>> staticDamageInstruction,
                                                      Optional<ArithmeticInstructionDataPair> dynamicDamageInstruction,
                                                      List<EventInvocation> asDamagedEntity) implements IControlableEventParams<LivingIncomingDamageEvent> {

    public static final MapCodec<LivingIncomingDealDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT).codec()
                                    .optionalFieldOf("damage_instruction")
                                    .forGetter(LivingIncomingDealDamageEventController::staticDamageInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("damage_dynamic_instruction")
                                    .forGetter(LivingIncomingDealDamageEventController::dynamicDamageInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damaged_entity", List.of())
                                    .forGetter(LivingIncomingDealDamageEventController::asDamagedEntity)
                    ).apply(instance, LivingIncomingDealDamageEventController::new)
            );

    @Override
    public void applyEventParams(LivingIncomingDamageEvent event, SkillContainer container) {
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

        asDamagedEntity.forEach(invocations -> invocations.execute(event.getEntity()));
    }
}