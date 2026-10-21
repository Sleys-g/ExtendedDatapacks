package sleys.efedp.neoforge.core.events.list.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import sleys.efedp.neoforge.core.events.IEventParams;
import sleys.sl.epicfight.model.weaponry.ShapeParticleEngine;
import sleys.sl.library.client.particle.emitters.SimpleParticleEmitter;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record WeaponShapeParticleEvent(ResourceLocation particle, float amount) implements IEventParams {

    public static final MapCodec<WeaponShapeParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("particle").forGetter(WeaponShapeParticleEvent::particle),
                    Codec.FLOAT.fieldOf("amount").forGetter(WeaponShapeParticleEvent::amount)
            ).apply(instance, WeaponShapeParticleEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        executeEvent(livingCaster);
    }

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntity livingCaster) {
        this.executeEvent(livingCaster);
    }

    private void executeEvent(LivingEntity livingCaster) {
        var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particle);
        if (!(particleType instanceof SimpleParticleType simpleParticle)) return;

        if (this.isInvalid(livingCaster.level(), ProcessSides.CLIENT,"Weapon Shape Particle Event")) {
            return;
        }

        ShapeParticleEngine.generateHandParticles(
                livingCaster, livingCaster.getUsedItemHand(),
                new SimpleParticleEmitter(simpleParticle),
                amount
        );
    }
}
