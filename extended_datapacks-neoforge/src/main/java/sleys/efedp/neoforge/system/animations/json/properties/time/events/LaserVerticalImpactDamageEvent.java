package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.neoforge.system.animations.json.properties.phase.PhaseStunType;
import sleys.efedp.neoforge.task.tick.TickTaskList;
import sleys.sl.library.network.task.STCTickTask;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public record LaserVerticalImpactDamageEvent(TargetMode targetMode, Float damage,
                                             Optional<StunType> stunType) implements IAnimationEventParams {

    private enum TargetMode {
        IMPACTED_TARGET(TargetMode::impactedTarget),
        ALL_IMPACTED_TARGET(TargetMode::allImpactedTarget),
        RANDOM_IMPACTED_TARGET(TargetMode::randomImpactedTarget);

        final BiConsumer<LivingEntityPatch<?>, LaserVerticalImpactDamageEvent> entityProcess;

        TargetMode(BiConsumer<LivingEntityPatch<?>, LaserVerticalImpactDamageEvent> entityProcess) {
            this.entityProcess = entityProcess;
        }

        private static final Codec<TargetMode> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );

        public void process(LivingEntityPatch<?> caster, LaserVerticalImpactDamageEvent event) {
            entityProcess.accept(caster, event);
        }

        private static void impactedTarget(LivingEntityPatch<?> caster, LaserVerticalImpactDamageEvent event) {
            var target = caster.getTarget();
            if (target == null) return;

            impactLaser(caster.getOriginal(), target, event.stunType.orElse(StunType.NONE), event.damage);
        }

        private static void allImpactedTarget(LivingEntityPatch<?> caster, LaserVerticalImpactDamageEvent event) {
            var targets = caster.getCurrentlyActuallyHitEntities();
            if (targets.isEmpty()) return;

            for (var target : targets) {
                if (target == null) continue;
                impactLaser(caster.getOriginal(), target, event.stunType.orElse(StunType.NONE), event.damage);
            }
        }

        private static void randomImpactedTarget(LivingEntityPatch<?> caster, LaserVerticalImpactDamageEvent event) {
            var targets = caster.getCurrentlyActuallyHitEntities();
            if (targets.isEmpty()) return;

            var target = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
            if (target == null) return;

            impactLaser(caster.getOriginal(), target, event.stunType.orElse(StunType.NONE), event.damage);
        }

        private static void impactLaser(LivingEntity livingCaster,
                                        LivingEntity livingTarget,
                                        StunType stunType, float damage) {

            var level = livingCaster.level();
            var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, livingTarget, 1, Vec3.ZERO);

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                    livingCaster,
                    new STCTickTask(TickTaskList.LASER_VERTICAL_IMPACT_TASK.toString(), buf -> {
                            buf.writeInt(livingCaster.getId());
                            buf.writeInt(livingTarget.getId());
                        }, null
                    )
            );

            var hitResult = LaserEventHelper.clipToBlocks(level, targetPos, targetPos);
            var hitLocation = hitResult.getLocation();
            var collider = LaserEventHelper.buildBeamCollider(targetPos, hitLocation, 0.25F);

            collider.getCollideEntities(livingCaster).forEach(entity -> {
                entity.hurt(EpicFightDamageSources.witherBeam(livingCaster).setStunType(stunType), damage);
                LaserEventHelper.impactBurstServer(level, entity.position());
            });
        }
    }

    public static final MapCodec<LaserVerticalImpactDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TargetMode.CODEC.optionalFieldOf("target_mode", TargetMode.IMPACTED_TARGET)
                            .forGetter(LaserVerticalImpactDamageEvent::targetMode),
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserVerticalImpactDamageEvent::damage),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(LaserVerticalImpactDamageEvent::stunType)
            ).apply(instance, LaserVerticalImpactDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Laser Vertical Target Event")) return;

        targetMode.process(patch, this);
        patch.removeHurtEntities();
    }
}
