package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.properties.time.AnimationsEventInvocation;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public record OnTargetEntityEvent(TargetMode targetMode, List<AnimationsEventInvocation> doEvents) implements IAnimationEventParams {

    private enum TargetMode {
        IMPACTED_TARGET(TargetMode::impactedTarget),
        ALL_IMPACTED_TARGET(TargetMode::allImpactedTarget),
        RANDOM_IMPACTED_TARGET(TargetMode::randomImpactedTarget);

        final BiConsumer<List<AnimationsEventInvocation>, LivingEntityPatch<?>> entityProcess;

        TargetMode(BiConsumer<List<AnimationsEventInvocation>, LivingEntityPatch<?>> entityProcess) {
            this.entityProcess = entityProcess;
        }

        private static final Codec<TargetMode> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );

        public void process(List<AnimationsEventInvocation> doEvents, LivingEntityPatch<?> caster) {
            entityProcess.accept(doEvents, caster);
        }

        private static void impactedTarget(List<AnimationsEventInvocation> doEvents, LivingEntityPatch<?> caster) {
            var target = caster.getTarget();
            var targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (targetPatch == null) return;
            doEvents.forEach(events -> events.execute(null, targetPatch));
        }

        private static void allImpactedTarget(List<AnimationsEventInvocation> doEvents, LivingEntityPatch<?> caster) {
            var targets = caster.getCurrentlyActuallyHitEntities();
            if (targets.isEmpty()) return;

            for (var target : targets) {
                if (target == null) continue;

                var targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                if (targetPatch == null) continue;

                doEvents.forEach(events -> events.execute(null, targetPatch));
            }
        }

        private static void randomImpactedTarget(List<AnimationsEventInvocation> doEvents, LivingEntityPatch<?> caster) {
            var targets = caster.getCurrentlyActuallyHitEntities();
            if (targets.isEmpty()) return;

            var target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
            if (target == null) return;

            var targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (targetPatch == null) return;
            doEvents.forEach(events -> events.execute(null, targetPatch));
        }
    }

    public static final MapCodec<OnTargetEntityEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TargetMode.CODEC.optionalFieldOf("target_mode", TargetMode.IMPACTED_TARGET).forGetter(OnTargetEntityEvent::targetMode),
                    AnimationsEventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(OnTargetEntityEvent::doEvents)
            ).apply(instance, OnTargetEntityEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.BOTH, "On Target Entity Event")) return;
        targetMode.process(doEvents, patch);
    }
}
