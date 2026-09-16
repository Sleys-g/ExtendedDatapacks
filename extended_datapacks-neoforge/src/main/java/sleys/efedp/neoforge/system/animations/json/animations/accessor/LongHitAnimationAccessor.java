package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.HitAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record LongHitAnimationAccessor(float transitionTime, String animationPath,
                                       AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<LongHitAnimation> {

    public static final MapCodec<LongHitAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(LongHitAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(LongHitAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, path, armature) ->
                    new LongHitAnimationAccessor(transition, path, armature.accessor)
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return HitAnimationAccessors.LONG_HIT;
    }

    @Override
    public AnimationManager.AnimationAccessor<LongHitAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                         IAnimationProperties<LongHitAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new LongHitAnimation(
                    transitionTime,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
