package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionDataPair;
import sleys.efedp.neoforge.core.control.ArithmeticInstructionPair;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.sl.epicfight.events.EFPlayerAttackSpeedEvent;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record AttackSpeedEventController(Optional<ArithmeticInstructionPair<Float>> staticInstruction,
                                         Optional<ArithmeticInstructionDataPair> dynamicInstruction) implements IControlableEventParams<EFPlayerAttackSpeedEvent.EFAttackSpeedEvent> {

    public static final MapCodec<AttackSpeedEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            ArithmeticInstructionPair.CODEC(Codec.FLOAT).codec()
                                    .optionalFieldOf("attack_speed_instruction")
                                    .forGetter(AttackSpeedEventController::staticInstruction),
                            ArithmeticInstructionDataPair.CODEC.codec()
                                    .optionalFieldOf("attack_speed_dynamic_instruction")
                                    .forGetter(AttackSpeedEventController::dynamicInstruction)
                    ).apply(instance, AttackSpeedEventController::new)
            );

    @Override
    public void applyEventParams(EFPlayerAttackSpeedEvent.EFAttackSpeedEvent event, SkillContainer container) {
        var attribute = event.getAttackSpeedInstance();
        if (attribute == null) return;

        if (staticInstruction.isEmpty() && dynamicInstruction.isEmpty()) return;

        var entity = container.getExecutor().getOriginal();

        float current = (float) event.getAttackSpeed();
        float result = current;

        if (staticInstruction.isPresent()) {
            var pair = staticInstruction.get();
            result = pair.getResult(current, pair.amountValue());
        }
        if (dynamicInstruction.isPresent()) {
            result = dynamicInstruction.get().getResult(current, entity);
        }

        event.setAttackSpeed(result);
    }
}