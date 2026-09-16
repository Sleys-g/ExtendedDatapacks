package sleys.efedp.neoforge.system.animations.json.properties.time.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

public record TranslateEvent(Optional<Double> lateral,
                             Optional<Double> vertical,
                             Optional<Double> avance) implements IAnimationEventParams {

    public static final MapCodec<TranslateEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.DOUBLE.optionalFieldOf("lateral").forGetter(TranslateEvent::lateral),
                    Codec.DOUBLE.optionalFieldOf("vertical").forGetter(TranslateEvent::vertical),
                    Codec.DOUBLE.optionalFieldOf("avance").forGetter(TranslateEvent::avance)
            ).apply(instance, TranslateEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.CLIENT,"Translate Event")) {
            return;
        }

        var yaw = livingCaster.getYRot();
        var offset = new Vec3(lateral.orElse(0.0), vertical.orElse(0.0), avance.orElse(0.0));
        var localPosition = offset.yRot(-yaw * Mth.DEG_TO_RAD);
        livingCaster.move(MoverType.SELF, localPosition);
    }
}
