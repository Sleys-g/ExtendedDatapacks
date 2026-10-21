package sleys.efedp.neoforge.core.control.list.status;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import sleys.efedp.neoforge.core.control.IControlableEventParams;
import sleys.efedp.neoforge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record ExpiredMobEffectEventController(Optional<Boolean> cancelEvent,
                                              Optional<Holder<MobEffect>> targetEffect,
                                              List<EventInvocation> ifTargetEffect) implements IControlableEventParams<MobEffectEvent.Expired> {

    public static final MapCodec<ExpiredMobEffectEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(ExpiredMobEffectEventController::cancelEvent),
                            BuiltInRegistries.MOB_EFFECT.holderByNameCodec()
                                    .optionalFieldOf("effect")
                                    .forGetter(ExpiredMobEffectEventController::targetEffect),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("do_if_match", List.of())
                                    .forGetter(ExpiredMobEffectEventController::ifTargetEffect)
                    ).apply(instance, ExpiredMobEffectEventController::new)
            );

    @Override
    public void applyEventParams(MobEffectEvent.Expired event, SkillContainer container) {
        if (event.getEffectInstance() == null) return;
        if (targetEffect.isPresent() && !event.getEffectInstance().is(targetEffect.get())) return;

        ifTargetEffect.forEach(invocations -> invocations.execute(null, container.getExecutor()));
        cancelEvent.ifPresent(event::setCanceled);
    }
}
