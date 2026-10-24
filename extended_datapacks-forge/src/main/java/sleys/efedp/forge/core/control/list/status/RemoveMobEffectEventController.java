package sleys.efedp.forge.core.control.list.status;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import sleys.efedp.forge.core.control.IControlableEventParams;
import sleys.efedp.forge.core.events.EventInvocation;
import yesman.epicfight.skill.SkillContainer;

import java.util.List;
import java.util.Optional;

public record RemoveMobEffectEventController(Optional<Boolean> cancelEvent,
                                             Optional<Holder<MobEffect>> targetEffect,
                                             List<EventInvocation> ifTargetEffect) implements IControlableEventParams<MobEffectEvent.Remove> {

    @SuppressWarnings("deprecation")
    public static final MapCodec<RemoveMobEffectEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.BOOL.optionalFieldOf("cancel_event")
                                    .forGetter(RemoveMobEffectEventController::cancelEvent),
                            BuiltInRegistries.MOB_EFFECT.holderByNameCodec()
                                    .optionalFieldOf("effect")
                                    .forGetter(RemoveMobEffectEventController::targetEffect),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("do_if_match", List.of())
                                    .forGetter(RemoveMobEffectEventController::ifTargetEffect)
                    ).apply(instance, RemoveMobEffectEventController::new)
            );

    @Override
    public void applyEventParams(MobEffectEvent.Remove event, SkillContainer container) {
        if (event.getEffectInstance() == null) return;
        if (targetEffect.isPresent() && !event.getEffectInstance().equals(targetEffect.get())) return;

        ifTargetEffect.forEach(invocations -> invocations.execute(null, container.getExecutor()));
        cancelEvent.ifPresent(event::setCanceled);
    }
}