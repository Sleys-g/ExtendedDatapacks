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
import yesman.epicfight.api.animation.types.EmoteAnimation;

public record EmoteAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                     IArmatureType armatureType
) implements IAnimationAccessor<EmoteAnimation> {

    public static final MapCodec<EmoteAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(EmoteAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(EmoteAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(EmoteAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(EmoteAnimationAccessor::armatureType)
            ).apply(instance, EmoteAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return StaticAnimationAccessorType.EMOTE;
    }

    @Override
    public AnimationManager.AnimationAccessor<EmoteAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<EmoteAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new EmoteAnimation(
                    transitionTime,
                    isRepeat,
                    accessor,
                    armatureType.armature()
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
