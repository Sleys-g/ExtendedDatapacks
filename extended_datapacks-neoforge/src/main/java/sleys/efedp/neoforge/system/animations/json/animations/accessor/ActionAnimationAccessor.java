package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.InteractionAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.phase.ArmatureType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record ActionAnimationAccessor(
        float transitionTime, float delay, String animationPath,
        AssetAccessor<? extends Armature> armature
) implements IAnimationAccessor<ActionAnimation> {

    public static final MapCodec<ActionAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(ActionAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("delay").forGetter(ActionAnimationAccessor::delay),
                    Codec.STRING.fieldOf("animation").forGetter(ActionAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, delay, path, armature) ->
                    new ActionAnimationAccessor(transition, delay, path, armature.accessor)
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return InteractionAnimationAccessors.ACTION;
    }

    @Override
    public AnimationManager.AnimationAccessor<ActionAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<ActionAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new ActionAnimation(
                    transitionTime,
                    delay,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
