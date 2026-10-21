package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record TakePreDamageEventController(Optional<ArithmeticInstructionPair<Float>> staticInstruction,
                                           Optional<ArithmeticInstructionDataPair> dynamicInstruction,
                                           List<EventInvocation> asDamageDealer) implements IControlableEventParams<LivingDamageEvent.Pre> {

    public static final MapCodec<TakePreDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec().optionalFieldOf("instruction")
                                    .forGetter(TakePreDamageEventController::staticInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec().optionalFieldOf("dynamic_instruction")
                                    .forGetter(TakePreDamageEventController::dynamicInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_dealer", List.of())
                                    .forGetter(TakePreDamageEventController::asDamageDealer)
                    ).apply(instance, TakePreDamageEventController::new)
            );

    @Override
    public void applyEventParams(LivingDamageEvent.Pre event, SkillContainer container) {
        var entity = container.getExecutor().getOriginal();
        var currentDamage = event.getNewDamage();

        if (staticInstruction.isPresent()) {
            var pair = staticInstruction.get();
            currentDamage = pair.getResult(currentDamage, pair.amountValue());
        }
        if (dynamicInstruction.isPresent()) {
            var pair = dynamicInstruction.get();
            currentDamage = pair.getResult(currentDamage, entity);
        }

        if (staticInstruction.isPresent() || dynamicInstruction.isPresent()) {
            event.setNewDamage(currentDamage);
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageDealer.forEach(invocations -> invocations.execute(dealerEntity));
    }
}