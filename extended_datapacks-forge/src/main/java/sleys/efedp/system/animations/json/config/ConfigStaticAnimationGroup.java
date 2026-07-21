package sleys.efedp.system.animations.json.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record ConfigStaticAnimationGroup(ResourceLocation animation) implements IConfigAnimation<StaticAnimation> {

    public static final MapCodec<ConfigStaticAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("animation")
                            .forGetter(ConfigStaticAnimationGroup::animation)
            ).apply(instance, ConfigStaticAnimationGroup::new)
    );

    @Override
    public AnimationGroupType virtualGroupType() {
        return AnimationGroupType.STATIC_GROUP;
    }

    @Override
    public void applyConfig(IAnimationProperty<StaticAnimation> property) {
        var animation = getAccessor().get();
        property.applyTo(animation);
    }
}
