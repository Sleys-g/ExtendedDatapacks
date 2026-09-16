package sleys.efedp.neoforge.system.animations.json.groups.virtual;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record VirtualStaticAnimationGroup(ResourceLocation realAnimation,
                                          ResourceLocation virtualAnimation) implements IAnimationVirtualization<StaticAnimation> {

    public static final MapCodec<VirtualStaticAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("real_animation")
                            .forGetter(VirtualStaticAnimationGroup::realAnimation),

                    ResourceLocation.CODEC.fieldOf("virtual_animation")
                            .forGetter(VirtualStaticAnimationGroup::virtualAnimation)
            ).apply(instance, VirtualStaticAnimationGroup::new)
    );

    @Override
    public IAnimationGroupType groupType() {
        return EpicFightAnimationGroups.STATIC_GROUP;
    }
}
