package sleys.efedp.forge.core.control.list.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingExperienceDropEventController(Optional<Boolean> cancelEvent,
                                                  Optional<ArithmeticInstructionPair<Integer>> staticInstruction,
                                                  Optional<ArithmeticInstructionDataPair> dynamicInstruction) implements IControlableEventParams<LivingExperienceDropEvent> {

    public static final MapCodec<LivingExperienceDropEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingExperienceDropEventController::cancelEvent),
                            ArithmeticInstructionPair.CODEC(Codec.INT).codec()
                                    .optionalFieldOf("instruction")
                                    .forGetter(LivingExperienceDropEventController::staticInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("dynamic_instruction")
                                    .forGetter(LivingExperienceDropEventController::dynamicInstruction)
                    ).apply(instance, LivingExperienceDropEventController::new)
            );

    @Override
    public void applyEventParams(LivingExperienceDropEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        var entity = container.getExecutor().getOriginal();
        var currentXp = event.getDroppedExperience();

        if (staticInstruction.isPresent()) {
            var pair = staticInstruction.get();
            currentXp = pair.getResult(currentXp, pair.amountValue());
        }
        if (dynamicInstruction.isPresent()) {
            var pair = dynamicInstruction.get();
            currentXp = pair.getResult(currentXp, entity);
        }

        if (staticInstruction.isPresent() || dynamicInstruction.isPresent()) {
            event.setDroppedExperience(currentXp);
        }
    }
}