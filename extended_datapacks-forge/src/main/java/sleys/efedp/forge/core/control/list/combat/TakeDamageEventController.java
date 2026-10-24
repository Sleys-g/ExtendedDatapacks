package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record TakeDamageEventController(Optional<ArithmeticInstructionPair<Float>> staticInstruction,
                                        Optional<ArithmeticInstructionDataPair> dynamicInstruction,
                                        List<EventInvocation> asDamageDealer) implements IControlableEventParams<LivingDamageEvent> {

    public static final MapCodec<TakeDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec().optionalFieldOf("instruction")
                                    .forGetter(TakeDamageEventController::staticInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec().optionalFieldOf("dynamic_instruction")
                                    .forGetter(TakeDamageEventController::dynamicInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_dealer", List.of())
                                    .forGetter(TakeDamageEventController::asDamageDealer)
                    ).apply(instance, TakeDamageEventController::new)
            );

    @Override
    public void applyEventParams(LivingDamageEvent event, SkillContainer container) {
        var entity = container.getExecutor().getOriginal();
        var currentDamage = event.getAmount();

        if (staticInstruction.isPresent()) {
            var pair = staticInstruction.get();
            currentDamage = pair.getResult(currentDamage, pair.amountValue());
        }
        if (dynamicInstruction.isPresent()) {
            var pair = dynamicInstruction.get();
            currentDamage = pair.getResult(currentDamage, entity);
        }

        if (staticInstruction.isPresent() || dynamicInstruction.isPresent()) {
            event.setAmount(currentDamage);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageDealer.forEach(invocations -> invocations.execute(dealerEntity));
    }
}