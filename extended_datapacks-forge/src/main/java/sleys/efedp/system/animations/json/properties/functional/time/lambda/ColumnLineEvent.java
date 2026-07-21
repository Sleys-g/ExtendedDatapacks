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

public record ColumnLineEvent(ParticleOptions particle, double length, double columnRadius,
                              double height, int columnsCount, int pointsPerColumn) implements IAnimationEventParams {

    public static final MapCodec<ColumnLineEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(ColumnLineEvent::particle),
                    Codec.DOUBLE.fieldOf("length").forGetter(ColumnLineEvent::length),
                    Codec.DOUBLE.fieldOf("column_radius").forGetter(ColumnLineEvent::columnRadius),
                    Codec.DOUBLE.fieldOf("height").forGetter(ColumnLineEvent::height),
                    Codec.INT.fieldOf("columns_count").forGetter(ColumnLineEvent::columnsCount),
                    Codec.INT.fieldOf("points_per_column").forGetter(ColumnLineEvent::pointsPerColumn)
            ).apply(instance, ColumnLineEvent::new)
    );
    
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Column Line Event")) return;

        Vec3 forward = caster.getLookAngle().multiply(1, 0, 1).normalize(); 
        Vec3 from = caster.position();
        Vec3 to = from.add(forward.scale(length));

        List<Vec3> basePoints = ParticleShapeHelper.line(Vec3.ZERO, to.subtract(from), columnsCount);
        for (Vec3 base : basePoints) {
            List<Vec3> column = ParticleShapeHelper.cone(columnRadius, columnRadius, height, 1, pointsPerColumn);
            for (Vec3 offset : column) {
                Vec3 world = from.add(base).add(offset);
                caster.level().addParticle(particle, world.x, world.y, world.z, 0, 0, 0);
            }
        }
    }
}
