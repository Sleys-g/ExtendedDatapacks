package sleys.efedp.neoforge.system.animations.json.animations.accessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import sleys.efedp.neoforge.system.animations.json.animations.registry.IAnimationAccessorType;
import sleys.efedp.neoforge.system.animations.json.animations.types.CombatAnimationAccessors;
import sleys.efedp.neoforge.system.animations.json.properties.phase.ArmatureType;
import sleys.efedp.neoforge.system.animations.json.properties.phase.AnimationPhase;
import sleys.efedp.neoforge.system.animations.json.properties.IAnimationProperties;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.ComboAttackAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;

import java.util.Arrays;

public record ComboAttackAccessor(float transitionTime, String animationPath,
                                  AssetAccessor<? extends Armature> armature,
                                  AnimationPhase... phases
) implements IAnimationAccessor<ComboAttackAnimation> {

    public static final MapCodec<ComboAttackAccessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("transition_time").forGetter(ComboAttackAccessor::transitionTime),
                    Codec.STRING.fieldOf("animation").forGetter(ComboAttackAccessor::animationPath),
                    ArmatureType.CODEC.fieldOf("armature").forGetter(r -> ArmatureType.fromAccessor(r.armature())),
                    AnimationPhase.CODEC.codec().listOf().fieldOf("phases").forGetter(r -> Arrays.asList(r.phases()))
            ).apply(instance, (transition, path, armature, phases) ->
                    new ComboAttackAccessor(transition, path, armature.accessor, phases.toArray(AnimationPhase[]::new))
            )
    );

    @Override
    public IAnimationAccessorType accessorType() {
        return CombatAnimationAccessors.COMBO_ATTACK;
    }

    @Override
    public AnimationManager.AnimationAccessor<ComboAttackAnimation> register(AnimationManager.AnimationBuilder builder,
                                                                             IAnimationProperties<ComboAttackAnimation> property) {

        ArmatureType armatureType = ArmatureType.fromAccessor(this.armature);
        return builder.nextAccessor(animationPath, (accessor) -> {
            var animation = new ComboAttackAnimation(
                    transitionTime,
                    accessor,
                    armature,
                    Arrays.stream(phases)
                            .map(animationPhase -> animationPhase.parseToEpicFightPhases(armatureType))
                            .toArray(AttackAnimation.Phase[]::new)
            );
            property.applyTo(animation);
            this.isSuccessful(accessor);
            return animation;
        });
    }
}
