package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

public record DealPostPlayerDamageEventController(List<EventInvocation> asDamagedEntity) implements IControlableEventParams<LivingDamageEvent.Post> {

    public static final MapCodec<DealPostPlayerDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damaged_entity", List.of())
                                    .forGetter(DealPostPlayerDamageEventController::asDamagedEntity)
                    ).apply(instance, DealPostPlayerDamageEventController::new)
            );
    
    @Override
    public void applyEventParams(LivingDamageEvent.Post event, SkillContainer container) {
        asDamagedEntity.forEach(invocations -> invocations.execute(event.getEntity()));
    }
}