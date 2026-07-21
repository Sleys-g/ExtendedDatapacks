package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import sleys.sl.epicfight.model.weaponry.ShapeParticleEngine;
import sleys.sl.library.particle.emitters.STCSimpleParticleEmitter;
import sleys.sl.library.particle.emitters.SimpleParticleEmitter;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record WeaponShapeParticleEvent(ResourceLocation particle, float amount) implements IAnimationEventParams {

    public static final MapCodec<WeaponShapeParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("particle").forGetter(WeaponShapeParticleEvent::particle),
                    Codec.FLOAT.fieldOf("amount").forGetter(WeaponShapeParticleEvent::amount)
            ).apply(instance, WeaponShapeParticleEvent::new)
    );

    @Override @SuppressWarnings("deprecation")
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particle);
        if (!(particleType instanceof SimpleParticleType simpleParticle)) return;

        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.CLIENT,"Weapon Shape Particle Event")) {
            return;
        }

        ShapeParticleEngine.generateHandParticles(
                livingCaster, livingCaster.getUsedItemHand(),
                new SimpleParticleEmitter(simpleParticle),
                amount
        );
    }
}
