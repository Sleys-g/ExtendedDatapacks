package sleys.efedp.forge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.forge.system.animations.json.properties.helpers.LaserEventHelper;
import sleys.efedp.forge.system.animations.json.properties.phase.PhaseStunType;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Optional;

public record LaserTargetDamageEvent(Float damage,
                                     Optional<StunType> stunType,
                                     Optional<Double> CasterLateral,
                                     Optional<Double> CasterVertical,
                                     Optional<Double> CasterAvance) implements IAnimationEventParams {

    public static final MapCodec<LaserTargetDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserTargetDamageEvent::damage),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(LaserTargetDamageEvent::stunType),

                    Codec.DOUBLE.optionalFieldOf("caster_lateral").forGetter(LaserTargetDamageEvent::CasterLateral),
                    Codec.DOUBLE.optionalFieldOf("caster_vertical").forGetter(LaserTargetDamageEvent::CasterVertical),
                    Codec.DOUBLE.optionalFieldOf("caster_avance").forGetter(LaserTargetDamageEvent::CasterAvance)
            ).apply(instance, LaserTargetDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser Target Event")) return;

        var livingTarget = patch.getTarget();
        if (livingTarget == null) return;

        var level = livingCaster.level();

        var casterPos = livingCaster.position().add(
                LaserEventHelper.resolveLocalOffset(
                        livingCaster,
                        new Vec3(CasterLateral.orElse(0.0), CasterVertical.orElse(0.0), CasterAvance.orElse(0.0))
                )
        );

        var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, patch.getTarget(), 20, Vec3.ZERO);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 1F);
            LaserEventHelper.renderBeam(level, casterPos, targetPos);
            return;
        }

        var hitResult = LaserEventHelper.clipToBlocks(level, casterPos, targetPos);
        var hitLocation = hitResult.getLocation();
        var collider = LaserEventHelper.buildBeamCollider(casterPos, hitLocation, 0.25F);

        collider.getCollideEntities(livingCaster).forEach(entity -> {
            entity.hurt(EpicFightDamageSources.witherBeam(livingCaster).setStunType(stunType.orElse(StunType.NONE)), damage);
            LaserEventHelper.impactBurstServer(level, entity.position());
        });
    }
}
