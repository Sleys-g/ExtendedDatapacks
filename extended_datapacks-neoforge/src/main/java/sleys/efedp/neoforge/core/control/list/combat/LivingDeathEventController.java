package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

public record LivingDeathEventController(List<EventInvocation> asKillerEntity) implements IControlableEventParams<LivingDeathEvent> {

    public static final MapCodec<LivingDeathEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_killer_entity", List.of())
                                    .forGetter(LivingDeathEventController::asKillerEntity)
                    ).apply(instance, LivingDeathEventController::new)
            );

    @Override
    public void applyEventParams(LivingDeathEvent event, SkillContainer container) {
        if (!(event.getSource().getEntity() instanceof LivingEntity entity)) return;
        asKillerEntity.forEach(invocations -> invocations.execute(entity));
    }
}