package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.LaserEventHelper;
import sleys.sl.epicfight.model.JointModelCordReader;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;

import java.util.Optional;

public record LaserJointTargetDamageEvent(Float damage,
                                          String joint,
                                          float poseTime,
                                          Optional<Double> CasterLateral,
                                          Optional<Double> CasterVertical,
                                          Optional<Double> CasterAvance) implements IAnimationEventParams {

    public static final MapCodec<LaserJointTargetDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserJointTargetDamageEvent::damage),

                    Codec.STRING.fieldOf("joint").forGetter(LaserJointTargetDamageEvent::joint),
                    Codec.FLOAT.fieldOf("pose_time").forGetter(LaserJointTargetDamageEvent::poseTime),

                    Codec.DOUBLE.optionalFieldOf("caster_lateral").forGetter(LaserJointTargetDamageEvent::CasterLateral),
                    Codec.DOUBLE.optionalFieldOf("caster_vertical").forGetter(LaserJointTargetDamageEvent::CasterVertical),
                    Codec.DOUBLE.optionalFieldOf("caster_avance").forGetter(LaserJointTargetDamageEvent::CasterAvance)
            ).apply(instance, LaserJointTargetDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser Joint Target Event")) return;

        var livingTarget = patch.getTarget();
        if (livingTarget == null) return;

        var level = livingCaster.level();

        var casterPos = JointModelCordReader
                .getJoinWorldCoords(patch, patch.getArmature().searchJointByName(joint), poseTime, Vec3.ZERO)
                .add(LaserEventHelper.resolveLocalOffset(
                        livingCaster,
                        new Vec3(CasterLateral.orElse(0.0), CasterVertical.orElse(0.0), CasterAvance.orElse(0.0))
                ));

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
            entity.hurt(EpicFightDamageSources.witherBeam(livingCaster), damage);
            LaserEventHelper.impactBurstServer(level, entity.position());
        });
    }
}
