package sleys.efedp.system.animations.json.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.system.animations.json.definitions.AnimationRegistryType;
import sleys.efedp.system.animations.json.properties.phase.ArmatureType;
import sleys.efedp.system.animations.json.properties.phase.AnimationPhase;
import sleys.efedp.system.animations.json.properties.IAnimationProperty;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

import java.util.Arrays;

public record DashAttackAnimationAccessor(float transitionTime, String animationPath,
                                          AssetAccessor<? extends Armature> armature,
                                          AnimationPhase... phases
) implements IAnimationAccessor<DashAttackAnimation> {

    public static final MapCodec<DashAttackAnimationAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(DashAttackAnimationAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(DashAttackAnimationAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature())),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new DashAttackAnimationAccessor(transition, path, armature.accessor, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public AnimationRegistryType accessorType() {
        return AnimationRegistryType.DASH_ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<DashAttackAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                            IAnimationProperty<DashAttackAnimation> property) {
        ArmatureType armatureType = ArmatureType.fromAccessor(this.armature);
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new DashAttackAnimation(
                    transitionTime,
                    accessor,
                    armature,
                    Arrays.stream(phases)
                            .map(animationPhase -> animationPhase.parseToEpicFightPhases(armatureType))
                            .toArray(AttackAnimation.Phase[]::new)
            );
            property.applyTo(animation);
            return animation;
        });
    }
}
