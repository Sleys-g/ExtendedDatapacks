package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.ActionAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.ArmatureTypeRegistry;
import sleys.efedp.neoforge.system.animations.json.properties.armature.registry.IArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.InvincibleAnimation;

public record InvincibleAnimationAccessor(float transitionTime,
                                          String animationPath,
                                          IArmatureType armatureType
) implements IAnimationAccessor<InvincibleAnimation> {

    public static final MapCodec<InvincibleAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transitionTime").forGetter(InvincibleAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(InvincibleAnimationAccessor::animationPath),
                    ArmatureTypeRegistry.CODEC.fieldOf("armature").forGetter(InvincibleAnimationAccessor::armatureType)
            ).apply(instance, InvincibleAnimationAccessor::new)
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return ActionAnimationAccessorType.INVINCIBLE;
    }

    @Override
    public AnimationManager.AnimationAccessor<InvincibleAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                        IAnimationProperties<InvincibleAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new InvincibleAnimation(
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
