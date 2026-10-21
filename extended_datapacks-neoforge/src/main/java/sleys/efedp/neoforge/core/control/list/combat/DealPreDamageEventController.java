package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record DealPreDamageEventController(Optional<ArithmeticInstructionPair<Float>> staticDamageInstruction,
                                           Optional<ArithmeticInstructionDataPair> dynamicDamageInstruction,
                                           List<EventInvocation> asDamagedEntity) implements IControlableEventParams<LivingDamageEvent.Pre> {

    public static final MapCodec<DealPreDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT).codec()
                                    .optionalFieldOf("damage_instruction")
                                    .forGetter(DealPreDamageEventController::staticDamageInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("damage_dynamic_instruction")
                                    .forGetter(DealPreDamageEventController::dynamicDamageInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damaged_entity", List.of())
                                    .forGetter(DealPreDamageEventController::asDamagedEntity)
                    ).apply(instance, DealPreDamageEventController::new)
            );
    
    @Override
    public void applyEventParams(LivingDamageEvent.Pre event, SkillContainer container) {
        var entity = container.getExecutor().getOriginal();
        float damage = event.getNewDamage();

        if (staticDamageInstruction.isPresent()) {
            var pair = staticDamageInstruction.get();
            damage = pair.getResult(damage, pair.amountValue());
        }

        if (dynamicDamageInstruction.isPresent()) {
            damage = dynamicDamageInstruction.get().getResult(damage, entity);
        }

        if (staticDamageInstruction.isPresent() || dynamicDamageInstruction.isPresent()) {
            event.setNewDamage(damage);
        }

        asDamagedEntity.forEach(invocations -> invocations.execute(event.getEntity()));
    }
}