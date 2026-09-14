package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.LaserEventHelper;
import sleys.efedp.system.animations.json.properties.phase.PhaseStunType;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Optional;

public record LaserVerticalTargetDamageEvent(Float damage,
                                             Optional<StunType> stunType) implements IAnimationEventParams {

    public static final MapCodec<LaserVerticalTargetDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserVerticalTargetDamageEvent::damage),
                    PhaseStunType.CODEC.optionalFieldOf("stun_type").forGetter(LaserVerticalTargetDamageEvent::stunType)
            ).apply(instance, LaserVerticalTargetDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser Vertical Target Event")) return;

        var livingTarget = patch.getTarget();
        if (livingTarget == null) return;

        var level = livingCaster.level();
        var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, livingTarget, 1, Vec3.ZERO);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 1F);
            var points = LaserEventHelper.computeGroundedLazerPath(level, targetPos, targetPos, 1.3, 3.0, 32.0);
            LaserEventHelper.renderVerticalLazers(level, points, 30);
            return;
        }

        var hitResult = LaserEventHelper.clipToBlocks(level, targetPos, targetPos);
        var hitLocation = hitResult.getLocation();
        var collider = LaserEventHelper.buildBeamCollider(targetPos, hitLocation, 0.25F);

        collider.getCollideEntities(livingCaster).forEach(entity -> {
            entity.hurt(EpicFightDamageSources.witherBeam(livingCaster).setStunType(stunType.orElse(StunType.NONE)), damage);
            LaserEventHelper.impactBurstServer(level, entity.position());
        });
    }
}
