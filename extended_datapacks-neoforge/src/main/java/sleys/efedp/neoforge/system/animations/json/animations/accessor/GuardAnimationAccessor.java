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
import yesman.epicfight.api.animation.types.GuardAnimation;

public record GuardAnimationAccessor(float transitionTime, float lockTime, String animationPath,
                                     IArmatureType armatureType) implements IAnimationAccessor<GuardAnimation> {

    public static final MapCodec<GuardAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(GuardAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("lock_time").forGetter(GuardAnimationAccessor::lockTime),
                    Codec.STRING.fieldOf("animation").forGetter(GuardAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(GuardAnimationAccessor::armatureType)
            ).apply(instance, GuardAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return StaticAnimationAccessorType.GUARD;
    }

    @Override
    public AnimationManager.AnimationAccessor<GuardAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperties<GuardAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new GuardAnimation(
                    transitionTime,
                    lockTime,
                    accessor,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
