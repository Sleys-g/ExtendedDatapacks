package sleys.efedp.forge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.animations.types.ActionAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;

public record ActionAnimationAccessor(
        float transitionTime, float delay, String animationPath,
        IArmatureType armatureType
) implements IAnimationAccessor<ActionAnimation> {

    public static final MapCodec<ActionAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(ActionAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("delay").forGetter(ActionAnimationAccessor::delay),
                    Codec.STRING.fieldOf("animation").forGetter(ActionAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(ActionAnimationAccessor::armatureType)
            ).apply(instance, ActionAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return ActionAnimationAccessorType.ACTION;
    }

    @Override
    public AnimationManager.AnimationAccessor<ActionAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<ActionAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new ActionAnimation(
                    transitionTime,
                    delay,
                    accessor,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
