package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.forge.system.animations.json.properties.phase.PhaseStunType;
import sleys.efedp.forge.task.coroutine.ClientLaserLineCoroutine;
import sleys.efedp.forge.task.coroutine.ServerLaserLineCoroutine;
import sleys.sl.library.execution.task.Coroutine;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

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
            var clientCoroutine = new ClientLaserLineCoroutine(level, points, delay.orElse(0), SPEED_MULTIPLIER);
            Coroutine.CLIENT.start(clientCoroutine);
            return;
        }

        var serverCoroutine = new ServerLaserLineCoroutine(
                level, livingCaster, stunType.orElse(StunType.NONE),
                points, damage, delay.orElse(0),
                SPEED_MULTIPLIER
        );
        Coroutine.SERVER.start(serverCoroutine);
    }
}