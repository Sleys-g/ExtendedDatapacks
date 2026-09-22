package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.HitAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.KnockdownAnimation;

public record KnockdownAnimationAccessor(float transitionTime, String animationPath,
                                         IArmatureType armatureType) implements IAnimationAccessor<KnockdownAnimation> {

    public static final MapCodec<KnockdownAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(KnockdownAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(KnockdownAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(KnockdownAnimationAccessor::armatureType)
            ).apply(instance, KnockdownAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return HitAnimationAccessors.KNOCKDOWN;
    }

    @Override
    public AnimationManager.AnimationAccessor<KnockdownAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                           IAnimationProperties<KnockdownAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new KnockdownAnimation(
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
