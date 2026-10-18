package sleys.efedp.neoforge.core.events.list.gameplay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.core.events.IAnimationEventParams;
import yesman.epicfight.api.animation.AnimationManager;
import sleys.efedp.neoforge.core.ProcessSides;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public record PlayAnimationEvent(ResourceLocation animation, Float transition) implements IAnimationEventParams {

    public static final MapCodec<PlayAnimationEvent> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("animation").forGetter(PlayAnimationEvent::animation),
                    Codec.FLOAT.fieldOf("transition_time").forGetter(PlayAnimationEvent::transition)
            ).apply(instance, PlayAnimationEvent::new)
    );

    @Override
    public <T extends StaticAnimation> void execute(AssetAccessor<T> accessor, LivingEntityPatch<?> patch) {
        var livingCaster = patch.getOriginal();
        if (this.isInvalid(livingCaster.level(), ProcessSides.SERVER,"Play Animation Event")) {
            return;
        }

        patch.getServerAnimator().playAnimation(AnimationManager.byKey(animation), transition);
    }
}
