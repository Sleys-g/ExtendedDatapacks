package sleys.efedp.neoforge.core.events.list.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.ExtendedDatapacks;
import sleys.efedp.neoforge.core.events.EventInvocation;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.sl.library.util.data.codec.EnumCodecs;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public record OnTargetEntityEvent(TargetMode targetMode, List<EventInvocation> doEvents) implements IEventParams {

    private enum TargetMode {
        IMPACTED_TARGET(TargetMode::impactedTarget),
        ALL_IMPACTED_TARGET(TargetMode::allImpactedTarget),
        RANDOM_IMPACTED_TARGET(TargetMode::randomImpactedTarget);

        final BiConsumer<List<EventInvocation>, LivingEntityPatch<?>> entityProcess;

        TargetMode(BiConsumer<List<EventInvocation>, LivingEntityPatch<?>> entityProcess) {
            this.entityProcess = entityProcess;
        }

        private static final Codec<TargetMode> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );

        public void process(List<EventInvocation> doEvents, LivingEntityPatch<?> caster) {
            entityProcess.accept(doEvents, caster);
        }

        private static void impactedTarget(List<EventInvocation> doEvents, LivingEntityPatch<?> caster) {
            var target = caster.getTarget();
            var targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
            if (targetPatch == null) return;
            doEvents.forEach(events -> events.execute(null, targetPatch));
        }

        private static void allImpactedTarget(List<EventInvocation> doEvents, LivingEntityPatch<?> caster) {
            var targets = caster.getCurrentlyActuallyHitEntities();
            if (targets.isEmpty()) return;

            for (var target : targets) {
                if (target == null) continue;

                var targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                if (targetPatch == null) continue;

                doEvents.forEach(events -> events.execute(null, targetPatch));
            }
        }

        private static void randomImpactedTarget(List<EventInvocation> doEvents, LivingEntityPatch<?> caster) {
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
                    EventInvocation.CODEC.listOf().optionalFieldOf("do", List.of()).forGetter(OnTargetEntityEvent::doEvents)
            ).apply(instance, OnTargetEntityEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, ProcessSides.BOTH, "On Target Entity Event")) return;
        targetMode.process(doEvents, patch);
        patch.removeHurtEntities();
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingEntity) {
        ExtendedDatapacks.LOGGER.warn("[On Target Entity Event] Unsupported operation! missing Patch Entity");
    }
}
