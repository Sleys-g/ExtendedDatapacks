package sleys.efedp.system.animations.json.properties.functional.time.lambda;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record TranslateEvent(Vec3 position) implements IAnimationEventParams {

    public static final MapCodec<TranslateEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("position").forGetter(TranslateEvent::position)
            ).apply(instance, TranslateEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), AnimationEvent.Side.CLIENT,"Translate Event")) {
            return;
        }

        livingCaster.move(MoverType.SELF, position);
    }
}
