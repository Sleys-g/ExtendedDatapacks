package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record GuardAnimationAccessor(float transitionTime, float lockTime, String animationPath,
                                     AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<GuardAnimation> {

    public static final MapCodec<GuardAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(GuardAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("lock_time").forGetter(GuardAnimationAccessor::lockTime),
                    Codec.STRING.fieldOf("animation").forGetter(GuardAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, lockTime,path, armature) ->
                    new GuardAnimationAccessor(transition, lockTime, path, armature.accessor)
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.GUARD;
    }

    @Override
    public AnimationManager.AnimationAccessor<GuardAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperty<GuardAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new GuardAnimation(
                    transitionTime,
                    lockTime,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
