package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record AttackEntityEventController(Optional<Boolean> cancelEvent,
                                          List<EventInvocation> asTarget) implements IControlableEventParams<AttackEntityEvent> {

    public static final MapCodec<AttackEntityEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(AttackEntityEventController::cancelEvent),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("as_target", List.of())
                                    .forGetter(AttackEntityEventController::asTarget)
                    ).apply(instance, AttackEntityEventController::new)
            );

    @Override
    public void applyEventParams(AttackEntityEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        if (asTarget.isEmpty()) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        asTarget.forEach(invocation -> invocation.execute(target));
    }
}