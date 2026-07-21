package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record DodgeAnimationAccessor(float transitionTime,
                                     float width, float height,
                                     String animationPath,
                                     AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<DodgeAnimation> {

    public static final MapCodec<DodgeAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(DodgeAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("width").forGetter(DodgeAnimationAccessor::width),
                    Codec.FLOAT.fieldOf("height").forGetter(DodgeAnimationAccessor::height),
                    Codec.STRING.fieldOf("animation").forGetter(DodgeAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, width, height,path, armature) ->
                    new DodgeAnimationAccessor(transition, width, height, path, armature.accessor)
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.DODGE;
    }

    @Override
    public AnimationManager.AnimationAccessor<DodgeAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperty<DodgeAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new DodgeAnimation(
                    transitionTime,
                    accessor,
                    width,
                    height,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
