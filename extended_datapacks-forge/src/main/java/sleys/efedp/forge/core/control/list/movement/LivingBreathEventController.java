package sleys.efedp.forge.core.control.list.movement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingBreathEventController(Optional<Boolean> forceCanBreathe,
                                          Optional<ArithmeticInstructionPair<Integer>> consumeAirInstruction,
                                          Optional<ArithmeticInstructionDataPair> consumeAirDynamicInstruction) implements IControlableEventParams<LivingBreatheEvent> {

    public static final MapCodec<LivingBreathEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("force_can_breathe")
                                    .forGetter(LivingBreathEventController::forceCanBreathe),
                            ArithmeticInstructionPair.CODEC(Codec.INT)
                                    .codec()
                                    .optionalFieldOf("consume_air_instruction")
                                    .forGetter(LivingBreathEventController::consumeAirInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("consume_air_dynamic_instruction")
                                    .forGetter(LivingBreathEventController::consumeAirDynamicInstruction)
                    ).apply(instance, LivingBreathEventController::new)
            );

    @Override
    public void applyEventParams(LivingBreatheEvent event, SkillContainer container) {
        if (forceCanBreathe.orElse(false)) event.setCanBreathe(true);

        var entity = container.getExecutor().getOriginal();
        var currentConsumeAir = event.getConsumeAirAmount();

        if (consumeAirInstruction.isPresent()) {
            var pair = consumeAirInstruction.get();
            currentConsumeAir = pair.getResult(currentConsumeAir, pair.amountValue());
        }

        if (consumeAirDynamicInstruction.isPresent()) {
            var pair = consumeAirDynamicInstruction.get();
            currentConsumeAir = pair.getResult(currentConsumeAir, entity);
        }

        if (consumeAirInstruction.isPresent() || consumeAirDynamicInstruction.isPresent()) {
            event.setConsumeAirAmount(currentConsumeAir);
        }
    }
}