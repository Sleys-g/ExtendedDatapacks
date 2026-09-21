package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.HitAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.phase.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.phase.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.LongHitAnimation;

public record LongHitAnimationAccessor(float transitionTime, String animationPath,
                                       IArmatureType armatureType) implements IAnimationAccessor<LongHitAnimation> {

    public static final MapCodec<LongHitAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(LongHitAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(LongHitAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(LongHitAnimationAccessor::armatureType)
            ).apply(instance, LongHitAnimationAccessor::new)
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
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
