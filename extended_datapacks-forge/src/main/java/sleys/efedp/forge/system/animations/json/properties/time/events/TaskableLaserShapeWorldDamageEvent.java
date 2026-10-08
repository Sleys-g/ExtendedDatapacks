package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserShapeHelper;
import sleys.efedp.forge.system.animations.json.properties.phase.PhaseStunType;
import sleys.efedp.forge.task.coroutine.ClientLaserShapeCoroutine;
import sleys.efedp.forge.task.coroutine.ServerLaserShapeCoroutine;
import sleys.sl.library.execution.task.Coroutine;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Optional;

public record TaskableLaserShapeWorldDamageEvent(Float damage, LaserShapeHelper.LaserShape shape, Float size,
                                                 Optional<Integer> delay, Optional<Float> spacing,
                                                 Optional<SpawnMode> spawnMode,
                                                 Optional<StunType> stunType,
                                                 Optional<Double> originLateral,
                                                 Optional<Double> originVertical,
                                                 Optional<Double> originAvance) implements IAnimationEventParams {

    private static final int SPEED_MULTIPLIER = 5;
    private static final int LASER_HEIGHT = 30;

    private enum SpawnMode {
        LOCAL(SpawnMode::resolveAsLocal),
        TARGET(SpawnMode::resolveAsTarget),
        DIRECTION(SpawnMode::resolveAsDirection),
        TARGET_OR_LOCAL(SpawnMode::resolveAsTargetOrLocal),
        TARGET_OR_DIRECTION(SpawnMode::resolveAsTargetOrDirection);

        private final TriFunction<LivingEntity, LivingEntity, TaskableLaserShapeWorldDamageEvent, Vec3> process;

        SpawnMode(TriFunction<LivingEntity, LivingEntity, TaskableLaserShapeWorldDamageEvent, Vec3> process) {
            this.process = process;
        }

        public static final Codec<SpawnMode> CODEC = EnumCodecs.byId(values(), Enum::name);

        public Vec3 resolvePoints(LivingEntity livingCaster, LivingEntity target,
                                  TaskableLaserShapeWorldDamageEvent event) {
            return process.apply(livingCaster, target, event);
        }

        private static Vec3 resolveAsLocal(LivingEntity livingCaster, LivingEntity target,
                                           TaskableLaserShapeWorldDamageEvent event) {
            return livingCaster.position().add(LaserEventHelper.resolveLocalOffset(
                    livingCaster,
                    new Vec3(
                            event.originLateral.orElse(0.0),
                            event.originVertical.orElse(0.0),
                            event.originAvance.orElse(0.0)
                    )
            ));
        }

        private static Vec3 resolveAsTarget(LivingEntity livingCaster, LivingEntity target,
                                            TaskableLaserShapeWorldDamageEvent event) {
            if (target == null) return null;
            return target.position().add(LaserEventHelper.resolveLocalOffset(
                    livingCaster,
                    new Vec3(
                            event.originLateral.orElse(0.0),
                            event.originVertical.orElse(0.0),
                            event.originAvance.orElse(0.0)
                    )
            ));
        }

        private static Vec3 resolveAsDirection(LivingEntity livingCaster, LivingEntity target,
                                               TaskableLaserShapeWorldDamageEvent event) {
            return LaserEventHelper.resolveAimPoint(
                    livingCaster, null, 7,
                    LaserEventHelper.resolveLocalOffset(
                            livingCaster,
                            new Vec3(
                                    event.originLateral.orElse(0.0),
                                    event.originVertical.orElse(0.0),
                                    event.originAvance.orElse(0.0)
                            )
                    )
            );
        }

        private static Vec3 resolveAsTargetOrLocal(LivingEntity livingCaster, LivingEntity target,
                                                   TaskableLaserShapeWorldDamageEvent event) {
            return target == null ?
                    resolveAsLocal(livingCaster, null, event) :
                    resolveAsTarget(livingCaster, target, event);
        }

        private static Vec3 resolveAsTargetOrDirection(LivingEntity livingCaster, LivingEntity target,
                                                       TaskableLaserShapeWorldDamageEvent event) {
            return target == null ?
                    resolveAsDirection(livingCaster, null, event) :
                    resolveAsTarget(livingCaster, target, event);
        }
    }

    public static final MapCodec<TaskableLaserShapeWorldDamageEvent> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("damage")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::damage),
                            LaserShapeHelper.LaserShape.CODEC.fieldOf("shape")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::shape),
                            Codec.FLOAT.fieldOf("size")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::size),
                            Codec.INT.optionalFieldOf("delay")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::delay),
                            Codec.FLOAT.optionalFieldOf("spacing")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::spacing),
                            SpawnMode.CODEC.optionalFieldOf("spawn_mode")
                                    .forGetter(TaskableLaserShapeWorldDamageEvent::spawnMode),
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

        var origin = spawnMode.orElse(SpawnMode.LOCAL).resolvePoints(livingCaster, patch.getTarget(), this);
        List<Vec3> points = LaserShapeHelper.generate(shape, origin, size, spacing.orElse(0.5F));
        points = LaserShapeHelper.rotateFacingCaster(points, livingCaster.position());
        points = LaserShapeHelper.groundPoints(level, points);

        int actualDelay = delay.orElse(0);
        var actualStunType = stunType.orElse(StunType.NONE);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);
            var clientCoroutine = new ClientLaserShapeCoroutine(level, points, actualDelay, LASER_HEIGHT, SPEED_MULTIPLIER);
            Coroutine.CLIENT.start(clientCoroutine);
            return;
        }

        var serverCoroutine = new ServerLaserShapeCoroutine(
                level, livingCaster, actualStunType,
                points, damage, actualDelay,
                LASER_HEIGHT, SPEED_MULTIPLIER
        );

        Coroutine.SERVER.start(serverCoroutine);
    }
}
