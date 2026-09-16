package sleys.efedp.forge.system.animations.json.groups.virtual;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.forge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.forge.system.animations.json.groups.registry.IAnimationGroupType;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record VirtualActionAnimationGroup(ResourceLocation realAnimation,
                                          ResourceLocation virtualAnimation) implements IAnimationVirtualization<ActionAnimation> {

    public static final MapCodec<VirtualActionAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("real_animation").forGetter(VirtualActionAnimationGroup::realAnimation),
                    ResourceLocation.CODEC.fieldOf("virtual_animation").forGetter(VirtualActionAnimationGroup::virtualAnimation)
            ).apply(instance, VirtualActionAnimationGroup::new)
    );

    @Override
    public IAnimationGroupType groupType() {
        return EpicFightAnimationGroups.ACTION_GROUP;
    }
}
