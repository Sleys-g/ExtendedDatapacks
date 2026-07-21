package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.ParticleShapeHelper;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public record ShapeParticleEvent(ParticleOptions particle, ParticleShape shape,
                                 double sizeX, double sizeY, double sizeZ, int points) implements IAnimationEventParams {

    public static final MapCodec<ShapeParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(ShapeParticleEvent::particle),
                    ParticleShape.CODEC.fieldOf("shape").forGetter(ShapeParticleEvent::shape),
                    Codec.DOUBLE.fieldOf("size_x").forGetter(ShapeParticleEvent::sizeX),
                    Codec.DOUBLE.fieldOf("size_y").forGetter(ShapeParticleEvent::sizeY),
                    Codec.DOUBLE.fieldOf("size_z").forGetter(ShapeParticleEvent::sizeZ),
                    Codec.INT.fieldOf("points").forGetter(ShapeParticleEvent::points)
            ).apply(instance, ShapeParticleEvent::new)
    );
    
    private enum ParticleShape {
        CUBE_OUTLINE, CUBE_FILL, SPHERE_OUTLINE, SPHERE_FILL, TORUS;

        private static final Codec<ParticleShape> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );
    }

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Shape Particles Event")) return;

        RandomSource rand = caster.getRandom();
        List<Vec3> shapePoints = switch (shape) {
            case CUBE_OUTLINE -> ParticleShapeHelper.boxOutline(sizeX, sizeY, sizeZ, points / 12);
            case CUBE_FILL -> ParticleShapeHelper.boxFill(sizeX, sizeY, sizeZ, points, rand);
            case SPHERE_OUTLINE -> ParticleShapeHelper.sphereFibonacci(sizeX, points);
            case SPHERE_FILL -> IntStream.range(0, points).mapToObj(i -> ParticleShapeHelper.randomInSphere(sizeX, rand)).toList();
            case TORUS -> ParticleShapeHelper.torus(sizeX, sizeY, points / 20, 20);
        };

        Vec3 origin = caster.position().add(0, caster.getBbHeight() / 2, 0);
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, origin, shapePoints, 0, false);
    }
}
