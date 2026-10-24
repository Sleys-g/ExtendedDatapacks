package sleys.efedp.forge.core.control.list.status;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingHealEventController(Optional<Boolean> cancelEvent,
                                        Optional<ArithmeticInstructionPair<Float>> staticInstruction,
                                        Optional<ArithmeticInstructionDataPair> dynamicInstruction) implements IControlableEventParams<LivingHealEvent> {

    public static final MapCodec<LivingHealEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingHealEventController::cancelEvent),
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec().optionalFieldOf("instruction")
                                    .forGetter(LivingHealEventController::staticInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec().optionalFieldOf("dynamic_instruction")
                                    .forGetter(LivingHealEventController::dynamicInstruction)
                    ).apply(instance, LivingHealEventController::new)
            );

    @Override
    public void applyEventParams(LivingHealEvent event, SkillContainer container) {
        cancelEvent.ifPresent(event::setCanceled);
        if (event.isCanceled()) return;
        var entity = container.getExecutor().getOriginal();

        var currentAmount = event.getAmount();

        if (staticInstruction.isPresent()) {
            var pair = staticInstruction.get();
            currentAmount = pair.getResult(currentAmount, pair.amountValue());
        }
        if (dynamicInstruction.isPresent()) {
            var pair = dynamicInstruction.get();
            currentAmount = pair.getResult(currentAmount, entity);
        }

        if (staticInstruction.isPresent() || dynamicInstruction.isPresent()) {
            event.setAmount(currentAmount);
        }
    }
}