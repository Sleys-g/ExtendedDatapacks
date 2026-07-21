package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record FractureGroundEvent(Vec3 edge, String jointName, Double radius, Float poseTime) implements IAnimationEventParams {

    public static final MapCodec<FractureGroundEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("edge").forGetter(FractureGroundEvent::edge),
                    Codec.STRING.fieldOf("joint").forGetter(FractureGroundEvent::jointName),
                    Codec.DOUBLE.fieldOf("radius").forGetter(FractureGroundEvent::radius),
                    Codec.FLOAT.fieldOf("pose_time").forGetter(FractureGroundEvent::poseTime)
            ).apply(instance, FractureGroundEvent::new)
    );
    
    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        Vec3 position = livingEntity.position();

        if (this.isInvalid(level, AnimationEvent.Side.CLIENT, "Fracture Ground")) {
            return;
        }

        OpenMatrix4f modelTransform = patch
                .getArmature()
                .getBoundTransformFor(
                        accessor.get().getPoseByTime(patch, poseTime, 1.0F),
                        patch.getArmature().searchJointByName(jointName)
                ).mulFront(OpenMatrix4f.createTranslation((float) position.x, (float) position.y, (float) position.z)
                        .mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
                                .mulBack(patch.getModelMatrix(1.0F))
                        )
                );

        Vec3 weaponEdge = OpenMatrix4f.transform(modelTransform, edge);
        BlockHitResult hitResult = level.clip(
                new ClipContext(position.add(0.0F, 0.1, 0.0F),
                        weaponEdge, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity
                )
        );

        Vec3 slamStartPos;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            Direction direction = hitResult.getDirection();
            BlockPos collidePos = hitResult.getBlockPos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            if (!LevelUtil.canTransferShockWave(level, collidePos, level.getBlockState(collidePos))) {
                collidePos = collidePos.below();
            }
            slamStartPos = new Vec3(collidePos.getX(), collidePos.getY(), collidePos.getZ());
        } else {
            slamStartPos = weaponEdge.subtract(0.0F, 1.0F, 0.0F);
        }

        LevelUtil.circleSlamFracture(
                livingEntity, level, slamStartPos, radius,
                false, false
        ); 
    }
}