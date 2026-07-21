package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import sleys.sl.epicfight.model.JointModelParticleEngine;
import sleys.sl.library.client.particle.emitters.SimpleParticleEmitter;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;

public record JointParticleEvent(ResourceLocation particle, Vec3 volume,
                                 List<Vec3> offSets, Vec3 motion, double step,
                                 String joint, float amount) implements IAnimationEventParams {

    public static final MapCodec<JointParticleEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("particle").forGetter(JointParticleEvent::particle),
                    Vec3.CODEC.fieldOf("volume").forGetter(JointParticleEvent::volume),
                    Vec3.CODEC.listOf().fieldOf("offsets").forGetter(JointParticleEvent::offSets),
                    Vec3.CODEC.fieldOf("motion").forGetter(JointParticleEvent::motion),
                    Codec.DOUBLE.fieldOf("step").forGetter(JointParticleEvent::step),
                    Codec.STRING.fieldOf("joint").forGetter(JointParticleEvent::joint),
                    Codec.FLOAT.fieldOf("amount").forGetter(JointParticleEvent::amount)
            ).apply(instance, JointParticleEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particle);
        if (!(particleType instanceof SimpleParticleType simpleParticle)) return;
        LivingEntity livingEntity = patch.getOriginal();
        JointModelParticleEngine.generateParticles(
                livingEntity, joint,
                offSets, step,
                volume, motion,
                new SimpleParticleEmitter(simpleParticle),
                amount
        );
    }
}
