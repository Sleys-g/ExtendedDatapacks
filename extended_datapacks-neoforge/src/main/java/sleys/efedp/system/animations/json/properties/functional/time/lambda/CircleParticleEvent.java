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

public record CircleParticleEvent(ParticleOptions particle, double radius, int points) implements IAnimationEventParams {

    public static final MapCodec<CircleParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(CircleParticleEvent::particle),
                    Codec.DOUBLE.fieldOf("radius").forGetter(CircleParticleEvent::radius),
                    Codec.INT.fieldOf("points").forGetter(CircleParticleEvent::points)
            ).apply(instance, CircleParticleEvent::new)
    );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Circle Event")) return;

        List<Vec3> shape = ParticleShapeHelper.circle(radius, points);
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, caster.position(), shape, 0, false);
    }
}
