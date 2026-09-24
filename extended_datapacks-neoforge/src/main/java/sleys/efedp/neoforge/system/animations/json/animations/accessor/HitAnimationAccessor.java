package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.StaticAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.HitAnimation;

public record HitAnimationAccessor(float transitionTime, String animationPath,
                                   IArmatureType armatureType) implements IAnimationAccessor<HitAnimation> {

    public static final MapCodec<HitAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(HitAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(HitAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(HitAnimationAccessor::armatureType)
            ).apply(instance, HitAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return StaticAnimationAccessorType.HIT;
    }

    @Override
    public AnimationManager.AnimationAccessor<HitAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                     IAnimationProperties<HitAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new HitAnimation(
                    transitionTime,
                    accessor,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
