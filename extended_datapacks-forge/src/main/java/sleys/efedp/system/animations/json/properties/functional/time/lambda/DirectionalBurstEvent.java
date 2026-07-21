package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.ParticleShapeHelper;
import sleys.sl.library.util.data.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.Locale;

public record DirectionalBurstEvent(ParticleOptions particle, int points, double speed,
                                    BurstShape shape, double angleWidth, double angleHeight) implements IAnimationEventParams {

    public static final MapCodec<DirectionalBurstEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(DirectionalBurstEvent::particle),
                    Codec.INT.fieldOf("points").forGetter(DirectionalBurstEvent::points),
                    Codec.DOUBLE.fieldOf("speed").forGetter(DirectionalBurstEvent::speed),
                    BurstShape.CODEC.fieldOf("shape").forGetter(DirectionalBurstEvent::shape),
                    Codec.DOUBLE.fieldOf("angle_width").forGetter(DirectionalBurstEvent::angleWidth),
                    Codec.DOUBLE.fieldOf("angle_height").forGetter(DirectionalBurstEvent::angleHeight)
            ).apply(instance, DirectionalBurstEvent::new)
    );
    
    private enum BurstShape {
        SPHERE, CONE, RECTANGLE;
        
        public static final Codec<BurstShape> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );
    }

    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Directional Burst Event")) return;

        Vec3 axis = caster.getLookAngle();
        List<Vec3> dirs = switch (shape) {
            case SPHERE -> ParticleShapeHelper.sphereFibonacci(1.0, points);
            case CONE -> ParticleShapeHelper.coneDirections(axis, angleWidth, points, caster.getRandom());
            case RECTANGLE -> ParticleShapeHelper.rectangleDirections(axis, angleWidth, angleHeight, points, caster.getRandom());
        };

        Vec3 origin = caster.position().add(0, caster.getBbHeight() / 2, 0);
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, origin, dirs, speed, true);
    }
}
