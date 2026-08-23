package sleys.efedp.system.animations.json.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sleys.efedp.system.animations.json.definitions.AnimationGroupType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record ConfigActionAnimationGroup(ResourceLocation animation) implements IConfigAnimation<ActionAnimation> {

    public static final MapCodec<ConfigActionAnimationGroup> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ResourceLocation.CODEC.fieldOf("animation")
                    .forGetter(ConfigActionAnimationGroup::animation))
            .apply(instance, ConfigActionAnimationGroup::new)
    );

    @Override
    public AnimationGroupType configGroupType() {
        return AnimationGroupType.ACTION_GROUP;
    }

    @Override
    public void applyConfig(IAnimationProperty<ActionAnimation> property) {
        if (this.isInvalidAccessor()) return;
        var animation = getAccessor().get();
        property.applyTo(animation);
        this.isSuccessful();
    }
}
