package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserShapeHelper;
import sleys.efedp.forge.system.animations.json.properties.phase.PhaseStunType;
import sleys.sl.library.annotations.Internal;
import sleys.sl.library.execution.task.Coroutine;
import sleys.sl.library.execution.task.CoroutineTask;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Optional;

public record TaskableLaserShapeWorldDamageEvent(Float damage, LaserShape shape, Float size,
                                                 Optional<Integer> delay, Optional<Float> spacing,
                                                 Optional<StunType> stunType,
                                                 Optional<Double> originLateral,
                                                 Optional<Double> originVertical,
                                                 Optional<Double> originAvance) implements IAnimationEventParams {

    @Internal
    public enum LaserShape {
        LINE, ARROW, X, CROSS,
        CIRCLE, RING, DISC,
        TRIANGLE, SQUARE, ARC,
        SEMICIRCLE, STAR, CONE;

        public static final Codec<LaserShape> CODEC = EnumCodecs.byId(values(), Enum::name);
    }

    private static final int SPEED_MULTIPLIER = 5;
    private static final int LASER_HEIGHT = 30;

    public static final MapCodec<TaskableLaserShapeWorldDamageEvent> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("damage")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::damage),
                            LaserShape.CODEC.fieldOf("shape")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::shape),
                            Codec.FLOAT.fieldOf("size")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::size),
                            Codec.INT.optionalFieldOf("delay")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::delay),
                            Codec.FLOAT.optionalFieldOf("spacing")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::spacing),
                            PhaseStunType.CODEC.optionalFieldOf("stun_type")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::stunType),
                            Codec.DOUBLE.optionalFieldOf("origin_lateral")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::originLateral),
                            Codec.DOUBLE.optionalFieldOf("origin_vertical")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::originVertical),
                            Codec.DOUBLE.optionalFieldOf("origin_avance")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::originAvance)
                    ).apply(instance, TaskableLaserShapeWorldDamageEvent::new)
            );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();

        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Taskable Laser Shape World Damage Event")) {
            return;
        }

        var level = livingCaster.level();
        var origin = livingCaster.position().add(LaserEventHelper.resolveLocalOffset(
                livingCaster,
                new Vec3(originLateral.orElse(0.0), originVertical.orElse(0.0), originAvance.orElse(0.0))
        ));

        List<Vec3> points = LaserShapeHelper.generate(shape, origin, size, spacing.orElse(0.5F));
        points = LaserShapeHelper.groundPoints(level, points);


        int actualDelay = delay.orElse(0);
        var actualStunType = stunType.orElse(StunType.NONE);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);
            var clientCoroutine = new ClientLaserCoroutine(level, points, actualDelay);
            Coroutine.CLIENT.start(clientCoroutine);
            return;
        }

        var serverCoroutine = new ServerLaserCoroutine(level, livingCaster, actualStunType, points, damage, actualDelay);
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
                LaserEventHelper.renderVerticalLazers(level, points, LASER_HEIGHT);
                return false;
            }

            for (int i = 0; i < SPEED_MULTIPLIER && index < points.size(); i++) this.renderPoint(points.get(index++));
            boolean hasMore = index < points.size();
            if (hasMore) this.waitTicks(delay);
            return hasMore;
        }

        private void renderPoint(Vec3 base) {
            LaserEventHelper.impactZoneClient(level, base);
            level.addAlwaysVisibleParticle(
                    EpicFightParticles.LASER.get(),
                    base.x, base.y, base.z,
                    base.x, base.y + LASER_HEIGHT, base.z
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
                LaserEventHelper.hurtAlongVerticalLazers(level, livingCaster, points, stunType, LASER_HEIGHT, 0.4F, damage);
                return false;
            }

            for (int i = 0; i < SPEED_MULTIPLIER && index < points.size(); i++) this.damagePoint(points.get(index++));
            boolean hasMore = index < points.size();
            if (hasMore) this.waitTicks(delay);
            return hasMore;
        }

        private void damagePoint(Vec3 base) {
            AABB pillar = new AABB(
                    base.x - 0.4F, base.y, base.z - 0.4F,
                    base.x + 0.4F, base.y + LASER_HEIGHT, base.z + 0.4F
            );

            level.getEntitiesOfClass(LivingEntity.class, pillar, entity -> entity != this.livingCaster)
                    .forEach(entity -> {
                        entity.hurt(EpicFightDamageSources.witherBeam(this.livingCaster).setStunType(stunType), damage);
                        LaserEventHelper.impactBurstServer(level, entity.position());
                    });
        }
    }
}
