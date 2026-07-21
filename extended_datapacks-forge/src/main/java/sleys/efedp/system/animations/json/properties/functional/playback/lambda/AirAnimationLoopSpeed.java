package sleys.efedp.system.animations.json.properties.functional.playback.lambda;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public record AirAnimationLoopSpeed() implements IPlaySpeedModifierParams {
    public static final MapCodec<AirAnimationLoopSpeed> CODEC = MapCodec.unit(AirAnimationLoopSpeed::new);

    private static <T extends DynamicAnimation> String getLastY(T self, LivingEntityPatch<?> patch) {
        return "last_y." + patch.getOriginal().getName().toString().toLowerCase(Locale.ROOT) +
                ".animation." + self.getRegistryName();
    }

    private static <T extends DynamicAnimation> String getStuckTicks(T self, LivingEntityPatch<?> patch) {
        return "stuck_ticks." + patch.getOriginal().getName().toString().toLowerCase(Locale.ROOT) +
                ".animation." + self.getRegistryName();
    }

    @Override
    public <T extends DynamicAnimation> float modify(T self, LivingEntityPatch<?> patch,
                                                     float speed, float prevElapsedTime,
                                                     float elapsedTime) {
        String getLastY = getLastY(self, patch);
        String getStuckTicks = getStuckTicks(self, patch);

        LivingEntity entity = patch.getOriginal();
        Level level = entity.level();

        double posX = entity.getX();
        double posY = entity.getY();
        double posZ = entity.getZ();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(posX, posY, posZ);
        BlockState block = level.getBlockState(pos);

        while (block.getCollisionShape(level, pos).isEmpty() && pos.getY() > level.getMinBuildHeight()) {
            pos.move(Direction.DOWN);
            block = level.getBlockState(pos);
        }

        double distanceToGround = Math.max(posY - pos.getY() - 1.0D, 0.0D);
        CompoundTag tag = entity.getPersistentData();

        double lastY = tag.getDouble(getLastY);
        int stuckTicks = tag.getInt(getStuckTicks);

        if (posY == lastY) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        tag.putDouble(getLastY, posY);
        tag.putInt(getStuckTicks, stuckTicks);

        boolean isStuck = stuckTicks > 10;

        if (distanceToGround > 1.2F) {
            if (elapsedTime > 0.3F && elapsedTime < 0.6F && !isStuck) {
                return 0.0001F;
            }

            return 0.6F;
        }

        tag.remove(getLastY);
        tag.remove(getStuckTicks);
        return speed;
    }
}
