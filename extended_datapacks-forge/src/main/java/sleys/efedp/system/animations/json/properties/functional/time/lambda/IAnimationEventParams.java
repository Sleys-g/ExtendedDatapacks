package sleys.efedp.system.animations.json.properties.functional.time.lambda;


import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import sleys.efedp.ExtendedDatapacks;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public sealed interface IAnimationEventParams permits CameraTransitionEvent, CircleParticleEvent, ColumnEvent, ColumnLineEvent, CommandAnimationEvent, DirectionalBurstEvent, EntityAfterImageEvent, FloorParticleEvent, FractureGroundEvent, InvulnerabilityAnimationEvent, JointParticleEvent, JointShootProjectileEvent, PlayAnimationEvent, RadialFloorExpandEvent, ShapeParticleEvent, SmallExplosionEvent, SummonEntityOnTargetEvent, TeleportEvent, ThunderAnimationEvent, TranslateEvent, WeaponShapeParticleEvent, WhiteAfterImageEvent {

    <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch);

    default boolean isInvalid(Level level, AnimationEvent.Side expectedSide, String task) {
        switch (expectedSide) {
            case CLIENT -> {
                if (level.isClientSide) {
                    return false;
                } else {
                    ExtendedDatapacks.LOGGER.error("[Animation Events Functions] The task: {}, can only be executed on the client side", task);
                }
            }
            case SERVER -> {
                if (!level.isClientSide) {
                    return false;
                } else {
                    ExtendedDatapacks.LOGGER.error("[Animation Events Functions] The task: {}, can only be executed on the server side", task);
                }
            }
            case LOCAL_CLIENT -> {
                if (!FMLEnvironment.dist.isDedicatedServer() && level.isClientSide) {
                    return false;
                } else {
                    ExtendedDatapacks.LOGGER.error("[Animation Events Functions] The task: {}, can only be executed on the logical client side", task);
                }
            }
            case BOTH -> {
                return false;
            }
        }
        return true;
    }
}