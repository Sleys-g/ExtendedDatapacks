package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.forge.system.animations.json.properties.phase.PhaseStunType;
import sleys.efedp.forge.task.coroutine.ClientLaserFloorCoroutine;
import sleys.efedp.forge.task.coroutine.ServerLaserFloorCoroutine;
import sleys.sl.library.execution.task.Coroutine;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.StunType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record TaskableLaserFloorWorldDamageEvent(Float damage, Optional<StunType> stunType,
                                                 Float radius, Integer count,
                                                 Optional<Integer> delay,
                                                 Optional<Integer> pointsPerTick) implements IAnimationEventParams {

    public static final MapCodec<TaskableLaserFloorWorldDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(TaskableLaserFloorWorldDamageEvent::damage),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(TaskableLaserFloorWorldDamageEvent::stunType),
                    Codec.FLOAT.fieldOf("radius").forGetter(TaskableLaserFloorWorldDamageEvent::radius),
                    Codec.INT.fieldOf("count").forGetter(TaskableLaserFloorWorldDamageEvent::count),
                    Codec.INT.optionalFieldOf("delay").forGetter(TaskableLaserFloorWorldDamageEvent::delay),
                    Codec.INT.optionalFieldOf("points_per_tick").forGetter(TaskableLaserFloorWorldDamageEvent::pointsPerTick)
            ).apply(instance, TaskableLaserFloorWorldDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Tickable Laser World Floor Event")) return;

        var level = livingCaster.level();
        var center = livingCaster.position();
        var random = level.getRandom();

        livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);
        List<List<Vec3>> pillars = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            double x = center.x + Math.cos(angle) * dist;
            double z = center.z + Math.sin(angle) * dist;

            double groundY = findGroundY(level, x, (int) center.y + 8, z);
            Vec3 base = new Vec3(x, groundY, z);

            pillars.add(LaserEventHelper.computeGroundedLazerPath(level, base, base, 1.3, 3.0, 32.0));
        }

        int delayTicks = delay.orElse(0);
        int perTick = Math.max(1, pointsPerTick.orElse(1));

        if (level.isClientSide) {
            var clientCoroutine = new ClientLaserFloorCoroutine(level, pillars, delayTicks, perTick);
            Coroutine.CLIENT.start(clientCoroutine);
            return;
        }

        var serverCoroutine = new ServerLaserFloorCoroutine(
                level, livingCaster, stunType.orElse(StunType.NONE),
                pillars, damage, delayTicks, perTick
        );
        Coroutine.SERVER.start(serverCoroutine);
    }

    private static double findGroundY(Level level, double x, int fromY, double z) {
        var pos = new BlockPos.MutableBlockPos((int) Math.floor(x), fromY, (int) Math.floor(z));
        while (pos.getY() > level.getMinBuildHeight() && level.isEmptyBlock(pos)) pos.move(0, -1, 0);
        return pos.getY() + 1;
    }
}