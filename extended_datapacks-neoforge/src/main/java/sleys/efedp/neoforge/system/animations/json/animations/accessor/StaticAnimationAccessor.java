package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.StaticAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;

public record StaticAnimationAccessor(float transitionTime, boolean isRepeat, String animationPath,
                                      IArmatureType armatureType
) implements IAnimationAccessor<StaticAnimation> {

    public static final MapCodec<StaticAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(StaticAnimationAccessor::transitionTime),
                    Codec.BOOL.fieldOf("is_repeat").forGetter(StaticAnimationAccessor::isRepeat),
                    Codec.STRING.fieldOf("animation").forGetter(StaticAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(StaticAnimationAccessor::armatureType)
            ).apply(instance, StaticAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return StaticAnimationAccessorType.STATIC;
    }

    @Override
    public AnimationManager.AnimationAccessor<StaticAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<StaticAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new StaticAnimation(
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
