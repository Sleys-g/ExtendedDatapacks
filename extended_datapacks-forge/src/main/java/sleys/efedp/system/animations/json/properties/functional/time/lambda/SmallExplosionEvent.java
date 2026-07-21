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

public record SmallExplosionEvent(ParticleOptions particle, int points,
                                  double speed, double spread) implements IAnimationEventParams {

    public static final MapCodec<SmallExplosionEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ParticleTypes.CODEC.fieldOf("particle").forGetter(SmallExplosionEvent::particle),
                    Codec.INT.fieldOf("points").forGetter(SmallExplosionEvent::points),
                    Codec.DOUBLE.fieldOf("speed").forGetter(SmallExplosionEvent::speed),
                    Codec.DOUBLE.fieldOf("spread").forGetter(SmallExplosionEvent::spread)
            ).apply(instance, SmallExplosionEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var caster = patch.getOriginal();
        if (this.isInvalid(caster.level(), AnimationEvent.Side.CLIENT, "Small Explosion Event")) return;

        List<Vec3> dirs = ParticleShapeHelper.sphereFibonacci(spread, points);
        Vec3 origin = caster.position().add(0, caster.getBbHeight() / 2, 0);
        ParticleShapeHelper.spawnAtPoints(caster.level(), particle, origin, dirs, speed, true);
    }
}
