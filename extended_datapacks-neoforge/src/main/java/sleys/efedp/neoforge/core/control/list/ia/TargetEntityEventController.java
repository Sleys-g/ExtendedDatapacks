package sleys.efedp.neoforge.core.control.list.ia;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import sleys.sl.epicfight.events.EFOnTargetEvent;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

public record TargetEntityEventController(List<EventInvocation> asTargetEntity) implements IControlableEventParams<EFOnTargetEvent.EpicFightTargetEvent> {

    public static final MapCodec<TargetEntityEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_target_entity", List.of())
                                    .forGetter(TargetEntityEventController::asTargetEntity)
                    ).apply(instance, TargetEntityEventController::new)
            );
    
    @Override
    public void applyEventParams(EFOnTargetEvent.EpicFightTargetEvent event, SkillContainer container) {
        asTargetEntity.forEach(invocations -> invocations.execute(event.getTarget()));
    }
}