package sleys.efedp.neoforge.system.animations.json.groups.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.neoforge.system.animations.json.groups.types.EpicFightAnimationGroups;
import sleys.efedp.neoforge.system.animations.json.groups.registry.IAnimationGroupType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record ConfigStaticAnimationGroup(ResourceLocation animation) implements IAnimationConfig<StaticAnimation> {

    public static final MapCodec<ConfigStaticAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("animation")
                            .forGetter(ConfigStaticAnimationGroup::animation)
            ).apply(instance, ConfigStaticAnimationGroup::new)
    );

    @Override
    public IAnimationGroupType groupType() {
        return EpicFightAnimationGroups.STATIC_GROUP;
    }

    @Override
    public void applyConfig(IAnimationProperties<StaticAnimation> property) {
        if (this.isInvalidAccessor()) return;
        var animation = getAccessor().get();
        property.applyTo(animation);
        this.isSuccessful();
    }
}
