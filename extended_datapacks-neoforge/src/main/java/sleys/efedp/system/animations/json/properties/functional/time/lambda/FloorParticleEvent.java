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

public record FloorParticleEvent(ParticleOptions particle, int points, double radius) implements IAnimationEventParams {

    public static final MapCodec<FloorParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(FloorParticleEvent::particle),
                    Codec.INT.fieldOf("points").forGetter(FloorParticleEvent::points),
                    Codec.DOUBLE.fieldOf("radius").forGetter(FloorParticleEvent::radius)
            ).apply(instance, FloorParticleEvent::new)
    );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Floor Particles Event")) return;

        List<Vec3> points_ = ParticleShapeHelper.diskFill(radius, points, caster.getRandom());
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, caster.position(), points_, 0, false);
    }
}
