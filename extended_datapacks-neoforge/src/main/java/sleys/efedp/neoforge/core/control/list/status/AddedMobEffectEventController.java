package sleys.efedp.neoforge.core.control.list.status;

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

public record AddedMobEffectEventController(Optional<Holder<MobEffect>> targetEffect,
                                            List<EventInvocation> ifTargetEffect) implements IControlableEventParams<MobEffectEvent.Added> {

    public static final MapCodec<AddedMobEffectEventController> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            BuiltInRegistries.MOB_EFFECT.holderByNameCodec()
                                    .optionalFieldOf("effect")
                                    .forGetter(AddedMobEffectEventController::targetEffect),
                            EventInvocation.CODEC.listOf()
                                    .optionalFieldOf("do_if_match", List.of())
                                    .forGetter(AddedMobEffectEventController::ifTargetEffect)
                    ).apply(instance, AddedMobEffectEventController::new)
            );

    @Override
    public void applyEventParams(MobEffectEvent.Added event, SkillContainer container) {
        if (targetEffect.isPresent() && !event.getEffectInstance().is(targetEffect.get())) return;
        ifTargetEffect.forEach(invocations -> invocations.execute(null, container.getExecutor()));
    }
}