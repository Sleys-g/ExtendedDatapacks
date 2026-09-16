package sleys.efedp.forge.system.animations.json.properties.time.events;

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
                var yaw = livingCaster.getYRot();
                var localOffset = offset.yRot(-yaw * Mth.DEG_TO_RAD);
                var finalPos = livingCaster.position().add(localOffset);
                livingCaster.teleportTo(finalPos.x, finalPos.y, finalPos.z);
            }
            case WORLD -> {
                var finalPos = livingCaster.position().add(offset);
                livingCaster.setPos(finalPos.x, finalPos.y, finalPos.z);
            }

            case TARGET -> {
                var target = patch.getTarget();
                if (target != null) {
                    var finalPos = target.position().add(offset);
                    livingCaster.teleportTo(finalPos.x, finalPos.y, finalPos.z);
                }
            }

            case TARGET_FRONT -> {
                var target = patch.getTarget();
                if (target != null) {
                    var targetYaw = target.getYRot();
                    var rotatedOffset = offset.yRot(-targetYaw * Mth.DEG_TO_RAD);
                    var finalPos = target.position().add(rotatedOffset);
                    livingCaster.teleportTo(finalPos.x, finalPos.y, finalPos.z);
                }
            }

            case TARGET_BEHIND -> {
                var target = patch.getTarget();
                if (target != null) {
                    var targetYaw = target.getYRot();
                    var behindOffset = new Vec3(offset.x, offset.y, -offset.z)
                            .yRot(-targetYaw * Mth.DEG_TO_RAD);
                    var finalPos = target.position().add(behindOffset);
                    livingCaster.teleportTo(finalPos.x, finalPos.y, finalPos.z);
                }
            }

            case GROUND -> {
                var targetXZ = livingCaster.position().add(offset.x, 0, offset.z);
                var highestGround = livingCaster.level().getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        BlockPos.containing(targetXZ)
                );
                livingCaster.teleportTo(targetXZ.x, highestGround.getY(), targetXZ.z);
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
