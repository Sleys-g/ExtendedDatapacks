package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

public record KnockdownAnimationAccessor(float transitionTime, String animationPath,
                                         AssetAccessor<? extends Armature> armature) implements IAnimationAccessor<KnockdownAnimation> {

    public static final MapCodec<KnockdownAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(KnockdownAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(KnockdownAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature()))
            ).apply(instance, (transition, path, armature) ->
                    new KnockdownAnimationAccessor(transition, path, armature.accessor)
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.KNOCKDOWN;
    }

    @Override
    public AnimationManager.AnimationAccessor<KnockdownAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                           IAnimationProperty<KnockdownAnimation> property) {
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new KnockdownAnimation(
                    transitionTime,
                    accessor,
                    armature
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
