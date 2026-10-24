package sleys.efedp.forge.core.control.list.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import sleys.efedp.forge.core.control.IControlableEventParams;
import yesman.epicfight.skill.SkillContainer;

import java.util.Optional;

public record LivingDropsEventController(Optional<Boolean> cancelEvent,
                                         Optional<Boolean> clearDrops) implements IControlableEventParams<LivingDropsEvent> {

    public static final MapCodec<LivingDropsEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(LivingDropsEventController::cancelEvent),
                            Codec.BOOL.optionalFieldOf("clear_drops")
                                    .forGetter(LivingDropsEventController::clearDrops)
                    ).apply(instance, LivingDropsEventController::new)
            );

    @Override
    public void applyEventParams(LivingDropsEvent event, SkillContainer container) {
        if (cancelEvent.orElse(false)) {
            event.setCanceled(true);
            return;
        }

        if (clearDrops.orElse(false)) event.getDrops().clear();
    }
}