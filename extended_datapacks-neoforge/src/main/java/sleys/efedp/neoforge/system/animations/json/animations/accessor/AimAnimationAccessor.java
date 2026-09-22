package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.CombatAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AimAnimation;

public record AimAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                   String animationMidPath, String animationUpPath,
                                   String animationDownPath, String animationLyingPath,
                                   IArmatureType armatureType) implements IAnimationAccessor<AimAnimation> {

    public static final MapCodec<AimAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(AimAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(AimAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(AimAnimationAccessor::animationPath),

                    Codec.STRING.fieldOf("animation_mid").forGetter(AimAnimationAccessor::animationMidPath),
                    Codec.STRING.fieldOf("animation_up").forGetter(AimAnimationAccessor::animationUpPath),
                    Codec.STRING.fieldOf("animation_down").forGetter(AimAnimationAccessor::animationDownPath),
                    Codec.STRING.fieldOf("animation_lying").forGetter(AimAnimationAccessor::animationLyingPath),

                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(AimAnimationAccessor::armatureType)
            ).apply(instance, AimAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return CombatAnimationAccessors.AIM;
    }

    @Override
    public AnimationManager.AnimationAccessor<AimAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperties<AimAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new AimAnimation(
                    transitionTime, isRepeat, accessor,
                    animationMidPath, animationUpPath,
                    animationDownPath, animationLyingPath,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
