package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sleys.efedp.neoforge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.neoforge.system.animations.json.properties.phase.PhaseStunType;
import sleys.sl.library.execution.task.Coroutine;
import sleys.sl.library.execution.task.CoroutineTask;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Optional;

public record TaskableLaserLineWorldDamageEvent(Float damage, Float range,
                                                Optional<Integer> delay,
                                                Optional<Float> spacing,
                                                Optional<StunType> stunType,
                                                Optional<Double> OriginLateral,
                                                Optional<Double> OriginVertical,
                                                Optional<Double> OriginAvance,
                                                Optional<Double> EndLateral,
                                                Optional<Double> EndVertical,
                                                Optional<Double> EndAvance) implements IAnimationEventParams  {

    private static final int SPEED_MULTIPLIER = 5;

    public static final MapCodec<TaskableLaserLineWorldDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(TaskableLaserLineWorldDamageEvent::damage),
                    Codec.FLOAT.fieldOf("range").forGetter(TaskableLaserLineWorldDamageEvent::range),
                    Codec.INT.optionalFieldOf("delay").forGetter(TaskableLaserLineWorldDamageEvent::delay),
                    Codec.FLOAT.optionalFieldOf("spacing").forGetter(TaskableLaserLineWorldDamageEvent::spacing),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(TaskableLaserLineWorldDamageEvent::stunType),

                    Codec.DOUBLE.optionalFieldOf("origin_lateral").forGetter(TaskableLaserLineWorldDamageEvent::OriginLateral),
                    Codec.DOUBLE.optionalFieldOf("origin_vertical").forGetter(TaskableLaserLineWorldDamageEvent::OriginVertical),
                    Codec.DOUBLE.optionalFieldOf("origin_avance").forGetter(TaskableLaserLineWorldDamageEvent::OriginAvance),

                    Codec.DOUBLE.optionalFieldOf("end_lateral").forGetter(TaskableLaserLineWorldDamageEvent::EndLateral),
                    Codec.DOUBLE.optionalFieldOf("end_vertical").forGetter(TaskableLaserLineWorldDamageEvent::EndVertical),
                    Codec.DOUBLE.optionalFieldOf("end_avance").forGetter(TaskableLaserLineWorldDamageEvent::EndAvance)
            ).apply(instance, TaskableLaserLineWorldDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Tickable Laser World Line Damage Event")) return;

        var level = livingCaster.level();

        var casterPos = livingCaster.position().add(LaserEventHelper.resolveLocalOffset(
                livingCaster,
                new Vec3(OriginLateral.orElse(0.0), OriginVertical.orElse(0.0), OriginAvance.orElse(0.0))
        ));

        var targetPos = LaserEventHelper.resolveAimPoint(
                livingCaster, null, range,
                LaserEventHelper.resolveLocalOffset(
                        livingCaster,
                        new Vec3(EndLateral.orElse(0.0), EndVertical.orElse(0.0), EndAvance.orElse(0.0))
                )
        );

        var points = LaserEventHelper.computeGroundedLazerPath(level, casterPos, targetPos, spacing.orElse(1.3F), 3.0, 32.0);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);
            var clientCoroutine = new ClientLaserCoroutine(level, points, delay.orElse(0));
            Coroutine.CLIENT.start(clientCoroutine);
            return;
        }

        var serverCoroutine = new ServerLaserCoroutine(
                level, livingCaster, stunType.orElse(StunType.NONE),
                points, damage, delay.orElse(0)
        );
        Coroutine.SERVER.start(serverCoroutine);
    }

    @OnlyIn(Dist.CLIENT)
    private static class ClientLaserCoroutine extends CoroutineTask {
        private final Level level;
        private final List<Vec3> points;
        private final int delay;
        private int index = 0;

        private ClientLaserCoroutine(Level level, List<Vec3> points, int delay) {
            this.level = level;
            this.points = points;
            this.delay = delay;
        }

        @Override
        protected boolean run() {
            if (delay == 0) {
                LaserEventHelper.renderVerticalLazers(level, points, 30);
                return false;
            }

            for (int i = 0; i < SPEED_MULTIPLIER && index < points.size(); i++) {
                this.renderPoint(points.get(index++));
            }

            boolean hasMore = index < points.size();

            if (hasMore) {
                this.waitTicks(delay);
            }

            return hasMore;
        }

        private void renderPoint(Vec3 base) {
            LaserEventHelper.impactZoneClient(level, base);
            level.addAlwaysVisibleParticle(
                    EpicFightParticles.LASER.get(),
                    base.x, base.y, base.z, base.x, base.y + 30, base.z
            );
        }
    }

    private static class ServerLaserCoroutine extends CoroutineTask {
        private final Level level;
        private final LivingEntity livingCaster;
        private final StunType stunType;
        private final List<Vec3> points;
        private final float damage;
        private final int delay;
        private int index = 0;

        private ServerLaserCoroutine(Level level, LivingEntity livingCaster, StunType stunType,
                                     List<Vec3> points, float damage, int delay) {
            this.level = level;
            this.livingCaster = livingCaster;
            this.stunType = stunType;
            this.points = points;
            this.damage = damage;
            this.delay = delay;
        }

        @Override
        protected boolean run() {
            if (delay == 0) {
                LaserEventHelper.hurtAlongVerticalLazers(
                        level, livingCaster, points,
                        stunType, 30,
                        0.4F, damage
                );
                return false;
            }

            for (int i = 0; i < SPEED_MULTIPLIER && index < points.size(); i++) {
                this.damagePoint(points.get(index++));
            }

            boolean hasMore = index < points.size();

            if (hasMore) {
                this.waitTicks(delay);
            }

            return hasMore;
        }

        private void damagePoint(Vec3 base) {
            AABB pillar = new AABB(
                    base.x - 0.4F, base.y, base.z - 0.4F,
                    base.x + 0.4F, base.y + 30, base.z + 0.4F
            );
            level.getEntitiesOfClass(LivingEntity.class, pillar, e -> e != this.livingCaster).forEach(entity -> {
                entity.hurt(EpicFightDamageSources.witherBeam(this.livingCaster).setStunType(stunType), damage);
                LaserEventHelper.impactBurstServer(level, entity.position());
            });
        }
    }
}