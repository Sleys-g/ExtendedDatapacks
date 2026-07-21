package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record MovementAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                        AssetAccessor<? extends Armature> armature
) implements IAnimationAccessor<MovementAnimation> {

    public static final MapCodec<MovementAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(MovementAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(MovementAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(MovementAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, repeat, path, armature) ->
                    new MovementAnimationAccessor(transition, repeat, path, armature.accessor)
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.MOVEMENT;
    }

    @Override
    public AnimationManager.AnimationAccessor<MovementAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                          IAnimationProperty<MovementAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new MovementAnimation(
                    transitionTime,
                    isRepeat,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
