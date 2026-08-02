package sleys.efedp.system.animations.json.virtual;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record VirtualStaticAnimationGroup(ResourceLocation realAnimation,
                                          ResourceLocation virtualAnimation) implements IVirtualAnimation<StaticAnimation> {

    public static final MapCodec<VirtualStaticAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("real_animation")
                            .forGetter(VirtualStaticAnimationGroup::realAnimation),

                    ResourceLocation.CODEC.fieldOf("virtual_animation")
                            .forGetter(VirtualStaticAnimationGroup::virtualAnimation)
            ).apply(instance, VirtualStaticAnimationGroup::new)
    );

    @Override
    public AnimationGroupType virtualGroupType() {
        return AnimationGroupType.STATIC_GROUP;
    }
}
