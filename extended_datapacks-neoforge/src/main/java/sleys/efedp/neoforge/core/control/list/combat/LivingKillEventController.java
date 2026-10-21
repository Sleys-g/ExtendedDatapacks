package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

public record LivingKillEventController(List<EventInvocation> asMurderedEntity) implements IControlableEventParams<LivingDeathEvent> {

    public static final MapCodec<LivingKillEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_murdered_entity", List.of())
                                    .forGetter(LivingKillEventController::asMurderedEntity)
                    ).apply(instance, LivingKillEventController::new)
            );

    @Override
    public void applyEventParams(LivingDeathEvent event, SkillContainer container) {
        asMurderedEntity.forEach(invocations -> invocations.execute(event.getEntity()));
    }
}