package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.registry.entries.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record EntityAfterImageEvent() implements IAnimationEventParams {
    public static final MapCodec<EntityAfterImageEvent> CODEC = MapCodec.unit(EntityAfterImageEvent::new);

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingEntity = patch.getOriginal();
        var level = livingEntity.level();
        if (this.isInvalid(level, AnimationEvent.Side.CLIENT, "Whiter AfterImage")) {
            return;
        }

        livingEntity.level().addParticle(
                EpicFightParticles.ENTITY_AFTER_IMAGE.get(),
                livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                Double.longBitsToDouble(livingEntity.getId()),
                0, 0
        );

        RandomSource random = livingEntity.level().getRandom();
        double x = livingEntity.getX() + (random.nextDouble() - random.nextDouble()) * 2.0D;
        double y = livingEntity.getY();
        double z = livingEntity.getZ() + (random.nextDouble() - random.nextDouble()) * 2.0D;

        livingEntity.level().addParticle(
                EpicFightParticles.ENTITY_AFTER_IMAGE.get(),
                x, y, z,
                random.nextDouble() * 0.005D,
                0.0D,
                0.0D
        );
    }
}
