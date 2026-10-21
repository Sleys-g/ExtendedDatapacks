package sleys.efedp.neoforge.core.control.list.combat;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;

public record TakePostDamageEventController(List<EventInvocation> asDamageDealer) implements IControlableEventParams<LivingDamageEvent.Post> {

    public static final MapCodec<TakePostDamageEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_damage_dealer", List.of())
                                    .forGetter(TakePostDamageEventController::asDamageDealer)
                    ).apply(instance, TakePostDamageEventController::new)
            );
    @Override
    public void applyEventParams(LivingDamageEvent.Post event, SkillContainer container) {
        if (!(event.getSource().getEntity() instanceof LivingEntity dealerEntity)) return;
        asDamageDealer.forEach(invocations -> invocations.execute(dealerEntity));
    }
}