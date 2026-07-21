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

public record ColumnEvent(ParticleOptions particle, double radius, double height,
                          int rings, int pointsPerRing) implements IAnimationEventParams {

    public static final MapCodec<ColumnEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(ColumnEvent::particle),
                    Codec.DOUBLE.fieldOf("radius").forGetter(ColumnEvent::radius),
                    Codec.DOUBLE.fieldOf("height").forGetter(ColumnEvent::height),
                    Codec.INT.fieldOf("rings").forGetter(ColumnEvent::rings),
                    Codec.INT.fieldOf("points_per_ring").forGetter(ColumnEvent::pointsPerRing)
            ).apply(instance, ColumnEvent::new)
    );

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Column Event Event")) return;

        List<Vec3> shape = ParticleShapeHelper.cone(radius, radius, height, rings, pointsPerRing);
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, caster.position(), shape, 0, false);
    }
}
