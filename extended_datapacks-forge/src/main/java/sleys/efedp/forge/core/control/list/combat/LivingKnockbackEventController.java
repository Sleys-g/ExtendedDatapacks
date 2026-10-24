package sleys.efedp.forge.core.control.list.combat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingKnockbackEventController(Optional<Boolean> cancelEvent) implements IControlableEventParams<LivingKnockBackEvent> {

    public static final MapCodec<LivingKnockbackEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingKnockbackEventController::cancelEvent)
                    ).apply(instance, LivingKnockbackEventController::new)
            );

    @Override
    public void applyEventParams(LivingKnockBackEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) event.setCanceled(true);
    }
}