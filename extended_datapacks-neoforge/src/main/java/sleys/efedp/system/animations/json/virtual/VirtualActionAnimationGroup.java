package sleys.efedp.system.animations.json.virtual;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record VirtualActionAnimationGroup(ResourceLocation realAnimation,
                                          ResourceLocation virtualAnimation) implements IVirtualAnimation<ActionAnimation> {

    public static final MapCodec<VirtualActionAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("real_animation").forGetter(VirtualActionAnimationGroup::realAnimation),
                    ResourceLocation.CODEC.fieldOf("virtual_animation").forGetter(VirtualActionAnimationGroup::virtualAnimation)
            ).apply(instance, VirtualActionAnimationGroup::new)
    );


    @Override
    public AnimationGroupType virtualGroupType() {
        return AnimationGroupType.ACTION_GROUP;
    }
}
