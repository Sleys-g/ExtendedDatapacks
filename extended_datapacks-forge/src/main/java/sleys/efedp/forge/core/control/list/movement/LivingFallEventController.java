package sleys.efedp.forge.core.control.list.movement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import sleys.efedp.forge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.forge.core.control.ArithmeticInstructionPair;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingFallEventController(Optional<Boolean> cancelEvent,
                                        Optional<ArithmeticInstructionPair<Float>> distanceInstruction,
                                        Optional<ArithmeticInstructionPair<Float>> damageMultiplierInstruction,
                                        Optional<ArithmeticInstructionDataPair> distanceDynamicInstruction,
                                        Optional<ArithmeticInstructionDataPair> damageMultiplierDynamicInstruction) implements IControlableEventParams<LivingFallEvent> {

    public static final MapCodec<LivingFallEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingFallEventController::cancelEvent),
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec()
                                    .optionalFieldOf("distance_instruction")
                                    .forGetter(LivingFallEventController::distanceInstruction),
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT)
                                    .codec()
                                    .optionalFieldOf("damage_multiplier_instruction")
                                    .forGetter(LivingFallEventController::damageMultiplierInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("distance_dynamic_instruction")
                                    .forGetter(LivingFallEventController::distanceDynamicInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("damage_multiplier_dynamic_instruction")
                                    .forGetter(LivingFallEventController::damageMultiplierDynamicInstruction)
                    ).apply(instance, LivingFallEventController::new)
            );

    @Override
    public void applyEventParams(LivingFallEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        var entity = container.getExecutor().getOriginal();

        var currentDistance = event.getDistance();

        if (distanceInstruction.isPresent()) {
            var pair = distanceInstruction.get();
            currentDistance = pair.getResult(currentDistance, pair.amountValue());
        }
        if (distanceDynamicInstruction.isPresent()) {
            var pair = distanceDynamicInstruction.get();
            currentDistance = pair.getResult(currentDistance, entity);
        }

        if (distanceInstruction.isPresent() || distanceDynamicInstruction.isPresent()) {
            event.setDistance(currentDistance);
        }

        var currentDamageMultiplier = event.getDamageMultiplier();

        if (damageMultiplierInstruction.isPresent()) {
            var pair = damageMultiplierInstruction.get();
            currentDamageMultiplier = pair.getResult(currentDamageMultiplier, pair.amountValue());
        }
        if (damageMultiplierDynamicInstruction.isPresent()) {
            var pair = damageMultiplierDynamicInstruction.get();
            currentDamageMultiplier = pair.getResult(currentDamageMultiplier, entity);
        }

        if (damageMultiplierInstruction.isPresent() || damageMultiplierDynamicInstruction.isPresent()) {
            event.setDamageMultiplier(currentDamageMultiplier);
        }
    }
}