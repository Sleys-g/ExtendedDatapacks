package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;
import sleys.efedp.system.animations.json.properties.functional.helpers.LaserEventHelper;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;

import java.util.Optional;

public record LaserTargetDamageEvent(Float damage,
                                     Optional<Vec3> finalOffset,
                                     Optional<Vec3> originOffset) implements IAnimationEventParams {

    public static final MapCodec<LaserTargetDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserTargetDamageEvent::damage),
                    Vec3.CODEC.optionalFieldOf("final_offset").forGetter(LaserTargetDamageEvent::finalOffset),
                    Vec3.CODEC.optionalFieldOf("origin_offset").forGetter(LaserTargetDamageEvent::originOffset)
            ).apply(instance, LaserTargetDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser Target Event")) return;

        var livingTarget = patch.getTarget();
        if (livingTarget == null) return;

        var level = livingCaster.level();
        var casterPos = livingCaster.position().add(originOffset.orElse(Vec3.ZERO));
        var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, patch.getTarget(), 20, finalOffset.orElse(Vec3.ZERO));

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
