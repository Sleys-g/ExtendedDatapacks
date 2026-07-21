package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import sleys.sl.library.util.data.codec.EnumCodecs;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Locale;

public record TeleportEvent(Vec3 offset, TeleportMode mode) implements IAnimationEventParams {

    public static final MapCodec<TeleportEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("offset").forGetter(TeleportEvent::offset),
                    TeleportMode.CODEC.fieldOf("mode").forGetter(TeleportEvent::mode)
            ).apply(instance, TeleportEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.SERVER, "Teleport Event")) {
            return;
        }

        switch (mode) {
            case LOCAL -> {
                float yaw = livingCaster.getYRot();
                Vec3 localOffset = offset.yRot(-yaw * Mth.DEG_TO_RAD);
                livingCaster.setPos(livingCaster.position().add(localOffset));
            }
            case WORLD -> livingCaster.setPos(livingCaster.position().add(offset));

            case TARGET -> {
                var target = patch.getTarget();
                if (target != null) {
                    livingCaster.setPos(target.position().add(offset));
                }
            }

            case TARGET_FRONT -> {
                var target = patch.getTarget();
                if (target != null) {
                    float targetYaw = target.getYRot();
                    Vec3 rotatedOffset = offset.yRot(-targetYaw * Mth.DEG_TO_RAD);
                    livingCaster.setPos(target.position().add(rotatedOffset));
                }
            }

            case TARGET_BEHIND -> {
                var target = patch.getTarget();
                if (target != null) {
                    float targetYaw = target.getYRot();
                    Vec3 behindOffset = new Vec3(offset.x, offset.y, -offset.z)
                            .yRot(-targetYaw * Mth.DEG_TO_RAD);
                    livingCaster.setPos(target.position().add(behindOffset));
                }
            }

            case GROUND -> {
                Vec3 targetXZ = livingCaster.position().add(offset.x, 0, offset.z);
                BlockPos highestGround = livingCaster.level().getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        BlockPos.containing(targetXZ)
                );
                livingCaster.setPos(targetXZ.x, highestGround.getY(), targetXZ.z);
            }
        }
    }

    private enum TeleportMode {
        LOCAL, WORLD, TARGET,
        TARGET_FRONT,
        TARGET_BEHIND,
        GROUND;

        public static final Codec<TeleportMode> CODEC = EnumCodecs.byId(values(),
                e -> e.name().toUpperCase(Locale.ROOT)
        );
    }
}
