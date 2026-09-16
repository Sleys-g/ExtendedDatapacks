package sleys.efedp.forge.system.animations.json.groups.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.forge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.forge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record ConfigActionAnimationGroup(ResourceLocation animation) implements IAnimationConfig<ActionAnimation> {

    public static final MapCodec<ConfigActionAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ResourceLocation.CODEC.fieldOf("animation")
                    .forGetter(ConfigActionAnimationGroup::animation))
            .apply(instance, ConfigActionAnimationGroup::new)
    );

    @Override
    public IAnimationGroupType groupType() {
        return EpicFightAnimationGroups.ACTION_GROUP;
    }

    @Override
    public void applyConfig(IAnimationProperties<ActionAnimation> property) {
        if (this.isInvalidAccessor()) return;
        var animation = getAccessor().get();
        property.applyTo(animation);
        this.isSuccessful();
    }
}
