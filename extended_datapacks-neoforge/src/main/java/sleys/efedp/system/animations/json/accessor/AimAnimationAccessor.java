package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AimAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record AimAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                   String animationMidPath, String animationUpPath,
                                   String animationDownPath, String animationLyingPath,
                                   AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<AimAnimation> {

    public static final MapCodec<AimAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(AimAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(AimAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(AimAnimationAccessor::animationPath),

                    Codec.STRING.fieldOf("animation_mid").forGetter(AimAnimationAccessor::animationMidPath),
                    Codec.STRING.fieldOf("animation_up").forGetter(AimAnimationAccessor::animationUpPath),
                    Codec.STRING.fieldOf("animation_down").forGetter(AimAnimationAccessor::animationDownPath),
                    Codec.STRING.fieldOf("animation_lying").forGetter(AimAnimationAccessor::animationLyingPath),

                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, repeat,
                               animation, animation_mid,
                               animation_up, animation_down,
                               animation_lying, armature) ->
                    new AimAnimationAccessor(
                            transition, repeat, animation,
                            animation_mid, animation_up, animation_down,
                            animation_lying, armature.accessor
                    )
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.AIM;
    }

    @Override
    public AnimationManager.AnimationAccessor<AimAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperty<AimAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new AimAnimation(
                    transitionTime, isRepeat, accessor,
                    animationMidPath, animationUpPath,
                    animationDownPath, animationLyingPath,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
