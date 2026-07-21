package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.HitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record HitAnimationAccessor(float transitionTime, String animationPath,
                                   AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<HitAnimation> {

    public static final MapCodec<HitAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(HitAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(HitAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, path, armature) ->
                    new HitAnimationAccessor(transition, path, armature.accessor)
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.HIT;
    }

    @Override
    public AnimationManager.AnimationAccessor<HitAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                     IAnimationProperty<HitAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new HitAnimation(
                    transitionTime,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
