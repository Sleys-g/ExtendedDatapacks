package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.ParticleShapeHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record RadialFloorExpandEvent(ParticleOptions particle, int points,
                                     double maxRadius, double speed) implements IAnimationEventParams {

    public static final MapCodec<RadialFloorExpandEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(RadialFloorExpandEvent::particle),
                    Codec.INT.fieldOf("points").forGetter(RadialFloorExpandEvent::points),
                    Codec.DOUBLE.fieldOf("max_radius").forGetter(RadialFloorExpandEvent::maxRadius),
                    Codec.DOUBLE.fieldOf("speed").forGetter(RadialFloorExpandEvent::speed)
            ).apply(instance, RadialFloorExpandEvent::new)
    );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Radial Floor Event")) return;
        List<Vec3> ringDirs = ParticleShapeHelper.circle(1.0, points);
        Vec3 origin = caster.position();
        for (Vec3 dir : ringDirs) {
            Vec3 spawnPos = origin.add(dir.scale(0.1));
            Vec3 velocity = dir.scale(speed);
            caster.level().addParticle(particle, spawnPos.x, spawnPos.y, spawnPos.z,
                    velocity.x, velocity.y, velocity.z);
        }
    }
}
