package sleys.efedp.forge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.forge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.animations.types.StaticAnimationAccessorType;
import sleys.efedp.forge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.forge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.MovementAnimation;

public record MovementAnimationAccessor(float transitionTime, boolean isRepeat,
                                        String animationPath,
                                        IArmatureType armatureType
) implements IAnimationAccessor<MovementAnimation> {

    public static final MapCodec<MovementAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(MovementAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(MovementAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(MovementAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(MovementAnimationAccessor::armatureType)
            ).apply(instance, MovementAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return StaticAnimationAccessorType.MOVEMENT;
    }

    @Override
    public AnimationManager.AnimationAccessor<MovementAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                          IAnimationProperties<MovementAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new MovementAnimation(
                    transitionTime,
                    isRepeat,
                    accessor,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
