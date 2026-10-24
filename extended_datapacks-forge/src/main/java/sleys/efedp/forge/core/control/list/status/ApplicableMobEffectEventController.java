package sleys.efedp.forge.core.control.list.status;

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

public record ApplicableMobEffectEventController(Optional<Holder<MobEffect>> targetEffect,
                                                 List<EventInvocation> ifTargetEffect) implements IControlableEventParams<MobEffectEvent.Applicable> {

    @SuppressWarnings("deprecation")
    public static final MapCodec<ApplicableMobEffectEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            BuiltInRegistries.MOB_EFFECT.holderByNameCodec()
                                    .optionalFieldOf("effect")
                                    .forGetter(ApplicableMobEffectEventController::targetEffect),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("do_if_match", List.of())
                                    .forGetter(ApplicableMobEffectEventController::ifTargetEffect)
                    ).apply(instance, ApplicableMobEffectEventController::new)
            );

    @Override
    public void applyEventParams(MobEffectEvent.Applicable event, SkillContainer container) {
        if (targetEffect.isPresent() && !event.getEffectInstance().equals(targetEffect.get())) return;
        ifTargetEffect.forEach(invocations -> invocations.execute(null, container.getExecutor()));
    }
}
