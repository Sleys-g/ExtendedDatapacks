package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.InteractionAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.DodgeAnimation;

public record DodgeAnimationAccessor(float transitionTime,
                                     float width, float height,
                                     String animationPath,
                                     IArmatureType armatureType) implements IAnimationAccessor<DodgeAnimation> {

    public static final MapCodec<DodgeAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(DodgeAnimationAccessor::transitionTime),
                    Codec.FLOAT.fieldOf("width").forGetter(DodgeAnimationAccessor::width),
                    Codec.FLOAT.fieldOf("height").forGetter(DodgeAnimationAccessor::height),
                    Codec.STRING.fieldOf("animation").forGetter(DodgeAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(DodgeAnimationAccessor::armatureType)
            ).apply(instance, DodgeAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return InteractionAnimationAccessors.DODGE;
    }

    @Override
    public AnimationManager.AnimationAccessor<DodgeAnimation> register(AnimationManager.AnimationBuilder builder, IAnimationProperties<DodgeAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new DodgeAnimation(
                    transitionTime,
                    accessor,
                    width,
                    height,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
