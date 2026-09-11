package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.LaserEventHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record LaserFloorWorldDamageEvent(Float damage, Float radius, Integer count) implements IAnimationEventParams {

    public static final MapCodec<LaserFloorWorldDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserFloorWorldDamageEvent::damage),
                    Codec.FLOAT.fieldOf("radius").forGetter(LaserFloorWorldDamageEvent::radius),
                    Codec.INT.fieldOf("count").forGetter(LaserFloorWorldDamageEvent::count)
            ).apply(instance, LaserFloorWorldDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser World Floor Event")) return;

        var level = livingCaster.level();
        Vec3 center = livingCaster.position();
        var random = level.getRandom();

        livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = random.nextDouble() * radius;
            double x = center.x + Math.cos(angle) * dist;
            double z = center.z + Math.sin(angle) * dist;

            double groundY = findGroundY(level, x, (int) center.y + 8, z);
            Vec3 base = new Vec3(x, groundY, z);

            var points = LaserEventHelper.computeGroundedLazerPath(level, base, base, 1.3, 3.0, 32.0);
            if (level.isClientSide) {
                LaserEventHelper.renderVerticalLazers(level, points, 30);
            } else {
                LaserEventHelper.hurtAlongVerticalLazers(level, livingCaster, points, 30, 0.4F, damage);
            }
        }
    }

    private static double findGroundY(Level level, double x, int fromY, double z) {
        var pos = new BlockPos.MutableBlockPos((int) Math.floor(x), fromY, (int) Math.floor(z));
        while (pos.getY() > level.getMinBuildHeight() && level.isEmptyBlock(pos)) pos.move(0, -1, 0);
        return pos.getY() + 1;
    }
}
