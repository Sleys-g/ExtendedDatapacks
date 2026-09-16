package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.LivingAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.phase.ArmatureType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record StaticAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                      AssetAccessor<? extends Armature> armature
) implements IAnimationAccessor<StaticAnimation> {

    public static final MapCodec<StaticAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(StaticAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(StaticAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(StaticAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, repeat, path, armature) ->
                    new StaticAnimationAccessor(transition, repeat, path, armature.accessor)
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return LivingAnimationAccessors.STATIC;
    }

    @Override
    public AnimationManager.AnimationAccessor<StaticAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<StaticAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new StaticAnimation(
                    transitionTime,
                    isRepeat,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
