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

public record LaserJointVerticalDamageEvent(Float damage,
                                            String joint,
                                            float poseTime,
                                            Optional<Vec3> originOffset) implements IAnimationEventParams {

    public static final MapCodec<LaserJointVerticalDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserJointVerticalDamageEvent::damage),
                    Codec.STRING.fieldOf("joint").forGetter(LaserJointVerticalDamageEvent::joint),
                    Codec.FLOAT.fieldOf("pose_time").forGetter(LaserJointVerticalDamageEvent::poseTime),
                    Vec3.CODEC.optionalFieldOf("origin_offset").forGetter(LaserJointVerticalDamageEvent::originOffset)
            ).apply(instance, LaserJointVerticalDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser Joint Vertical Event")) return;

        var level = livingCaster.level();
        var casterPos = JointModelCordReader
                .getJoinWorldCoords(patch, patch.getArmature().searchJointByName(joint), poseTime, Vec3.ZERO)
                .add(originOffset.orElse(Vec3.ZERO));
        var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, null, 1, Vec3.ZERO);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 1F);
            var points = LaserEventHelper.computeGroundedLazerPath(level, targetPos, targetPos, 1.3, 3.0, 32.0);
            LaserEventHelper.renderVerticalLazers(level, points, 30);
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
