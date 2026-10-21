package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record LivingShieldBlockedEventController(Optional<ArithmeticInstructionPair<Float>> staticDamageInstruction,
                                                 Optional<ArithmeticInstructionDataPair> dynamicDamageInstruction,
                                                 List<EventInvocation> asDamageDealer
) implements IControlableEventParams<LivingShieldBlockEvent> {

    public static final MapCodec<LivingShieldBlockedEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec().optionalFieldOf("damage_instruction")
                                    .forGetter(LivingShieldBlockedEventController::staticDamageInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec().optionalFieldOf("damage_dynamic_instruction")
                                    .forGetter(LivingShieldBlockedEventController::dynamicDamageInstruction),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_dealer", List.of())
                                    .forGetter(LivingShieldBlockedEventController::asDamageDealer)
                    ).apply(instance, LivingShieldBlockedEventController::new)
            );

    @Override
    public void applyEventParams(LivingShieldBlockEvent event, SkillContainer container) {
        var entity = container.getExecutor().getOriginal();
        var currentBlockedDamage = event.getBlockedDamage();

        if (staticDamageInstruction.isPresent()) {
            var pair = staticDamageInstruction.get();
            currentBlockedDamage = pair.getResult(currentBlockedDamage, pair.amountValue());
        }
        if (dynamicDamageInstruction.isPresent()) {
            var pair = dynamicDamageInstruction.get();
            currentBlockedDamage = pair.getResult(currentBlockedDamage, entity);
        }

        if (staticDamageInstruction.isPresent() || dynamicDamageInstruction.isPresent()) {
            event.setBlockedDamage(currentBlockedDamage);
        }

        if (!(event.getDamageSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageDealer.forEach(invocations -> invocations.execute(dealerEntity));
    }
}