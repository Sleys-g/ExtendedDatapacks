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

import java.util.Optional;

public record LaserLineWorldDamageEvent(Float damage, Float range,
                                        Optional<Vec3> finalOffset,
                                        Optional<Vec3> originOffset) implements IAnimationEventParams {

    public static final MapCodec<LaserLineWorldDamageEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter(LaserLineWorldDamageEvent::damage),
                    Codec.FLOAT.fieldOf("range").forGetter(LaserLineWorldDamageEvent::range),
                    Vec3.CODEC.optionalFieldOf("final_offset").forGetter(LaserLineWorldDamageEvent::finalOffset),
                    Vec3.CODEC.optionalFieldOf("origin_offset").forGetter(LaserLineWorldDamageEvent::originOffset)
            ).apply(instance, LaserLineWorldDamageEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.BOTH, "Laser World Line Damage Event")) return;

        var level = livingCaster.level();
        var casterPos = livingCaster.position().add(originOffset.orElse(Vec3.ZERO));
        var targetPos = LaserEventHelper.resolveAimPoint(livingCaster, null, range, finalOffset.orElse(Vec3.ZERO));

        var points = LaserEventHelper.computeGroundedLazerPath(level, casterPos, targetPos, 1.3, 3.0, 32.0);

        if (level.isClientSide) {
            livingCaster.playSound(EpicFightSounds.LASER_BLAST.get(), 0.5F, 0.2F);
            LaserEventHelper.renderVerticalLazers(level, points, 30);
            return;
        }

        LaserEventHelper.hurtAlongVerticalLazers(level, livingCaster, points, 30, 0.4F, damage);
    }
}
