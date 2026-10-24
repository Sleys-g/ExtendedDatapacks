package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record DealPlayerDamageEventController(Optional<ArithmeticInstructionPair<Float>> staticDamageInstruction,
                                              Optional<ArithmeticInstructionDataPair> dynamicDamageInstruction,
                                              List<EventInvocation> asDamagedEntity) implements IControlableEventParams<LivingDamageEvent> {

    public static final MapCodec<DealPlayerDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT).codec()
                                    .optionalFieldOf("damage_instruction")
                                    .forGetter(DealPlayerDamageEventController::staticDamageInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("damage_dynamic_instruction")
                                    .forGetter(DealPlayerDamageEventController::dynamicDamageInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damaged_entity", List.of())
                                    .forGetter(DealPlayerDamageEventController::asDamagedEntity)
                    ).apply(instance, DealPlayerDamageEventController::new)
            );

    @Override
    public void applyEventParams(LivingDamageEvent event, SkillContainer container) {
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